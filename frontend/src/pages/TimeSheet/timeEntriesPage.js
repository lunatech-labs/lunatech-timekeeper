import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Badge, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';
import moment from 'moment';
import UserTimeSheetList from '../../components/TimeSheet/UserTimeSheetList';
import MonthCalendar from '../../components/TimeSheet/MonthCalendar';
import CalendarSelectionMode from '../../components/TimeSheet/CalendarSelectionMode';

const TimeEntriesPage = () => {
  const [calendarMode, setCalendarMode] = useState('week');
  const firstDayOfCurrentWeek = moment().utc().startOf('week').add(1, 'day');
  const today = () => firstDayOfCurrentWeek.clone();
  const [currentWeekNumber, setCurrentWeekNumber] = useState(() => {
    const tmpDate = firstDayOfCurrentWeek.clone();
    return tmpDate.year() + '?weekNumber=' + tmpDate.isoWeek();
  });
  const [visibleEntryModal, setVisibleEntryModal] = useState(false);
  const [mode, setMode] = useState('view'); //Can be 'view', 'add' or 'edit'
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
  const timeSheets = dataFromServer.loading ? [] : dataFromServer.data.sheets;
  const days = Object.entries(groupBy(timeSheets.flatMap(({entries, project}) => entries.map(x => ({
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

  const resetForm = () => form.resetFields();
  const openModal = () => setVisibleEntryModal(true);
  const closeModal = () => setVisibleEntryModal(false);


  // A day without entries in the past should be displayed with "warn" design
  const hasWarnNoEntryInPastDay =(date,day) => {
    return moment().subtract('1','days').isAfter(date) && !day;
  };

  const onClickAddTask = (e, m) => {
    setTaskMoment(m);
    setAddMode();
    openModal();
  };

  const onClickCard = (e, m) => {
    setTaskMoment(m);
    setViewMode();
    openModal();
  };

  const removeDuplicatesElementAndSort = (array) => {
    const set = new Set(array);
    const arraySorted = [...set].sort((a, b) => a.localeCompare(b));
    return arraySorted;
  };

  const setViewMode = () => setMode('view');
  const setAddMode = () => setMode('add');

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
        <TimeEntryForm setMode={setMode} entries={entriesOfSelectedDay.map(entries => entries.data)} currentDay={taskMoment} form={form} mode={mode} onSuccess={() => {
          closeModal();
          dataFromServer.run();
          setViewMode();
        }} onCancel={() => setViewMode()}
        />
      </Modal>
      <UserTimeSheetList timeSheets={timeSheets}/>

      <CalendarSelectionMode onChange={e => setCalendarMode(e.target.value)}/>
      {
        calendarMode === 'week' ?
          <WeekCalendar
            firstDay={datas.firstDayOfWeek}
            disabledWeekEnd={true}
            hiddenButtons={false}
            onPanelChange={(id, start) => setCurrentWeekNumber(start.year() + '?weekNumber=' + start.isoWeek())}
            onClickButton={onClickAddTask}
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
            warningCardPredicate={hasWarnNoEntryInPastDay}
          /> :
          <MonthCalendar
            onClickButton={onClickAddTask}
            days={datas.days}
            dateCellRender={(data, date) => {
              const projectsName = data.filter(data => !!data).map(item => item.project.name);
              return removeDuplicatesElementAndSort(projectsName).map(item =>
                <div key={`badge-entry-project-${item}-${date.format('YYYY-MM-DD')}`}>
                  <Badge status='success' text={item}/>
                </div>);
            }}
            disabledWeekEnd={true}
            warningCardPredicate={hasWarnNoEntryInPastDay}
          />
      }


    </MainPage>
  );
};

export default TimeEntriesPage;

