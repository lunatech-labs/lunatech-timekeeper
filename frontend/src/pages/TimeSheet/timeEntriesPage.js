import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Badge, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';
import moment from 'moment';
import UserTimeSheetList from '../../components/TimeSheet/UserTimeSheetList';
import MonthCalendar from "../../components/TimeSheet/MonthCalendar";


const TimeEntriesPage = () => {
  const firstDayOfCurrentWeek = moment().utc().startOf('week').add(1, 'day');
  const today = () => firstDayOfCurrentWeek.clone();
  const [currentWeekNumber, setCurrentWeekNumber] = useState(() => {
    const tmpDate = firstDayOfCurrentWeek.clone();
    return tmpDate.year() + '?weekNumber=' + tmpDate.isoWeek();
  });
  const [visibleEntryModal, setVisibleEntryModal] = useState(false);
  const [viewMode, setViewMode] = useState(false);
  const [taskMoment, setTaskMoment] = useState(moment().utc());
  const [form] = Form.useForm();

  const dataFromServer = useTimeKeeperAPI('/api/my/' + currentWeekNumber);
  const {run} = dataFromServer;
  useEffect(
    () => {
      run();
    }, [currentWeekNumber, run]);

  if (dataFromServer.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the list of TimeSheets for this user'
          type='error'
          description='check that the authenticated User has role [user] on Quarkus'
        />
      </React.Fragment>
    );
  }

  //https://stackoverflow.com/questions/14446511/most-efficient-method-to-groupby-on-an-array-of-objects/34890276#34890276
  const groupBy = function (xs, key) {
    return xs.reduce(function (rv, x) {
      (rv[key(x)] = rv[key(x)] || []).push(x);
      return rv;
    }, {});
  };
  const timeEntries = dataFromServer.loading ? [] : dataFromServer.data.sheets;
  const days = Object.entries(groupBy(timeEntries.flatMap(({entries, project}) => entries.map(x => ({
    ...x,
    project
  }))), entry => moment(entry.startDateTime).format('YYYY-MM-DD'))).map(([key, value]) => {
    return ({
      data: value,
      date: moment(key),
      disabled: false
    });
  });

  const datas = {
    firstDayOfWeek: dataFromServer.data ? moment.utc(dataFromServer.data.firstDayOfWeek) : today(),
    days: days
  };

  const entriesOfSelectedDay = days.filter(day => day.date.format('YYYY-MM-DD') === taskMoment.format('YYYY-MM-DD'));

  const openModal = () => setVisibleEntryModal(true);
  const closeModal = () => setVisibleEntryModal(false);
  const resetForm = () => form.resetFields();

  // A day without entries in the past should be displayed with "warn" design
  const hasWarnNoEntryInPastDay =(date,day) => {
    return moment().subtract('1','days').isAfter(date) && !day;
  };

  const onClickAddTask = (e, m) => {
    setTaskMoment(m);
    setViewMode(false);
    openModal();
  };

  const onClickCard = (e, m) => {
    setTaskMoment(m);
    setViewMode(true);
    openModal();
  }

  return (
    <MainPage title="Time entries">
      <Modal
        visible={visibleEntryModal}
        onCancel={closeModal}
        destroyOnClose={true}
        afterClose={resetForm}
        width={'37.5%'}
        footer={null}
      >
        <TimeEntryForm setViewMode={setViewMode} entries={entriesOfSelectedDay.map(entries => entries.data)} currentDay={taskMoment} form={form} viewMode={viewMode} onSuccess={() => {
          closeModal();
          dataFromServer.run();
        }} onCancel={() => setViewMode(true)}/>
      </Modal>
      <UserTimeSheetList timeSheets={timeEntries}/>

      <MonthCalendar
        onClickAddTask={onClickAddTask}
        onSelect={moment => onClickCard(null , moment)}
        days={datas.days}
        dateCellRender={(data) => data.filter(data => !!data).map(item =>
          <div>
            <Badge status='success' text={item.project.name}/>
          </div>)}
        disabledWeekEnd={true}
      />

      <WeekCalendar
        firstDay={datas.firstDayOfWeek}
        disabledWeekEnd={true}
        hiddenButtons={false}
        onPanelChange={(id, start) => setCurrentWeekNumber(start.year() + '?weekNumber=' + start.isoWeek())}
        onClickAddTask={onClickAddTask}
        onClickCard={onClickCard}
        dateCellRender={(data) => {
          return (
            <div>
              {data.filter(data => !!data).map(entry => {
                return (
                  <TimeEntry key={entry.id} entry={entry}/>
                );
              })}
            </div>
          );
        }}
        days={datas.days}
        warningCardPredicate={(date, day) => hasWarnNoEntryInPastDay(date,day)}
      />
    </MainPage>
  );
};

export default TimeEntriesPage;

