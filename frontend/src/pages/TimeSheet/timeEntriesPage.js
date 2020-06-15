import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';
import moment from 'moment';
import UserTimeSheetList from '../../components/TimeSheet/UserTimeSheetList';


const TimeEntriesPage = () => {
  const firstDayOfCurrentWeek = moment().utc().startOf('week').add(1, 'day');
  const today = () => firstDayOfCurrentWeek.clone();
  const [currentFirstDay, setCurrentFirstDay] = useState(today);
  const [currentWeekNumber, setCurrentWeekNumber] = useState(() => {
    const tmpDate = firstDayOfCurrentWeek.clone();
    return tmpDate.year() + '?weekNumber=' + tmpDate.isoWeek();
  });
  const [visibleEntryModal, setVisibleEntryModal] = useState(false);
  const [taskMoment, setTaskMoment] = useState(moment().utc());
  const [form] = Form.useForm();

  const dataFromServer = useTimeKeeperAPI('/api/my/' + currentWeekNumber);
  useEffect(
    () => {
      dataFromServer.run();
    }, [currentWeekNumber]);

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
  const data = dataFromServer.loading ? [] : dataFromServer.data.sheets;
  const datas = Object.entries(groupBy(data.flatMap(({entries, project}) => entries.map(x => ({
    ...x,
    project
  }))), entry => moment(entry.startDateTime).format('YYYY-MM-DD'))).map(([key, value]) => {
    return ({
      data: value,
      date: moment(key),
      disabled: false
    });
  });

  const openModal = () => setVisibleEntryModal(true);
  const closeModal = () => setVisibleEntryModal(false);
  const resetForm = () => form.resetFields();
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
        <TimeEntryForm currentDay={taskMoment} form={form} onSuccess={() => {
          closeModal();
          dataFromServer.run();
        }} onCancel={closeModal}/>
      </Modal>
      <UserTimeSheetList timeSheets={data}/>

      <WeekCalendar
        firstDay={currentFirstDay}
        disabledWeekEnd={true}
        hiddenButtons={false}
        onPanelChange={(id, start) => {
          setCurrentFirstDay(start); // TODO voir si c'est encore utile
          setCurrentWeekNumber(start.year() + '?weekNumber=' + start.isoWeek()); // TODO sinon c'est mieux avec le numero de semaine
        }}
        onClickAddTask={(e, m) => {
          setTaskMoment(m);
          openModal();
        }}
        dateCellRender={(data) => {
          return (
            <div>
              {data.map(entry => {
                if (entry) {
                  return (
                    <TimeEntry entry={entry}/>
                  );
                } else {
                  return null;
                }
              })}
            </div>
          );
        }}
        days={datas}
      />
    </MainPage>
  );
};

export default TimeEntriesPage;

