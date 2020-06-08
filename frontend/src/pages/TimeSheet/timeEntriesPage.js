import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
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
        return;
      }
    }, [currentFirstDay]);

  const staticData = [
    {
      date: today(),
      disabled: true,
      data: [{
        name: 'First Day of the week is a day off',
      }]
    },
    {
      date: today().add(2, 'day'),
      disabled: false,
      data: [{
        name: 'It is wednesday my dudes',
        description: 'First element of the day',
        dateTime: today().add(4, 'hour')
      }, {
        name: 'It is wednesday my dudes',
        description: 'Second element of the day',
        dateTime: today().add(8, 'hour')
      }]
    }
  ];
  const staticDataWeek2 = [
    {
      date: today().add(1, 'week'),
      disabled: false,
      data: [{
        name: 'First Day of the second week',
      }]
    },
    {
      date: today().add(2, 'day').add(1, 'week'),
      disabled: false,
      data: [{
        name: 'It is wednesday my dudes of week 2',
        description: 'First element of the day',
        dateTime: today().add(1, 'week').add(4, 'hour')
      }, {
        name: 'It is wednesday my dudes of week 2',
        description: 'Second element of the day',
        dateTime: today().add(1, 'week').add(8, 'hour')
      }]
    }
  ];
  const data = [staticData, staticDataWeek2];

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
                    <div key={`badge-entry-${entry.dateTime && entry.dateTime.format('yyyy-mm-dd-hh-mm')}`}>
                      <Badge
                        status={(entry && entry.name) ? 'success' : 'error'}
                        text={(entry && entry.name) ? `name : ${entry.name} ${entry.dateTime && `(${entry.dateTime.format('hh:mm')})`}` : 'Nothing to render'}
                      />
                    </div>
                  );
                } else {
                  return null;
                }
              })}
            </div>
          );
        }}
        days={data[0]}
      />
    </MainPage>
  );
};

export default TimeEntriesPage;

