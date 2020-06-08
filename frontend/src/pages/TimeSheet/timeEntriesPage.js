import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert} from 'antd';
import {Badge, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';

const moment = require('moment');

const TimeEntriesPage = () => {
  const firstDayOfCurrentWeek = moment().startOf('week').add(1, 'day');
  const today = () => firstDayOfCurrentWeek.clone();
  const [currentFirstDay, setCurrentFirstDay] = useState(today);
  const [visibleEntryModal, setVisibleEntryModal] = useState(false);
  const [taskMoment, setTaskMoment] = useState(moment());
  const [form] = Form.useForm();
  useEffect(
    () => {
      //TODO : Fetch your data or select your week
      if (!currentFirstDay) {

      }
    }, [currentFirstDay]);

  const {data, loading, error} = useTimeKeeperAPI('/api/my/currentWeek');

  if (loading) {
    return (
      <div>loading...</div>
    );
  }

  if (error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
               message='Failed to load the list of clients'
               type='error'
               description='check that the authenticated User has role [user] on Quarkus'
        />
      </React.Fragment>
    );
  }

  if (data) {
    console.log('week:', JSON.stringify(data));
  }

  //https://stackoverflow.com/questions/14446511/most-efficient-method-to-groupby-on-an-array-of-objects/34890276#34890276
  const groupBy = function (xs, key) {
    return xs.reduce(function (rv, x) {
      (rv[key(x)] = rv[key(x)] || []).push(x);
      return rv;
    }, {});
  };
  const datas = Object.entries(groupBy(data.sheets.flatMap(({entries, project}) => entries.map(x => ({
    ...x,
    project
  }))), entry => moment((entry.endDateTime)))).map(([key, value]) => ({
    data: value,
    date: moment(key),
    disabled: false
  }));

  console.log('groupBY',
    datas
  );


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
        <TimeEntryForm moment={taskMoment} form={form} onSuccess={closeModal} onCancel={closeModal}/>
      </Modal>

      <WeekCalendar
        firstDay={currentFirstDay}
        disabledWeekEnd={true}
        hiddenButtons={false}
        onPanelChange={(id, start) => setCurrentFirstDay(start)}
        dateCellRender={(data, date, disabled) => {
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

