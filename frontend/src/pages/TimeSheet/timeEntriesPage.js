import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';

const moment = require('moment');

const TimeEntriesPage = () => {
  const firstDayOfCurrentWeek = moment().startOf('week').add(1, 'day');
  const today = () => firstDayOfCurrentWeek.clone();
  const [currentFirstDay, setCurrentFirstDay] = useState(today);

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
        dateTime: today().add(8, 'hour')
      }]
    },
    {
      date: today().add(1, 'day'),
      disabled: false,
      data: [{
        name: 'It is tuesday my dudes',
        description: 'First element of the day',
        project: {
          id: 1,
          name: 'Agira'
        },
        dateTime: today().add(4, 'hour')
      },{
        name: 'It is tuesday my dudes',
        description: 'Second element of the day',
        project: {
          id: 1,
          name: 'Google'
        },
        dateTime: today().add(2, 'hour')
      },{
        name: 'It is tuesday my dudes',
        description: 'Second element of the day',
        project: {
          id: 1,
          name: 'SPACE X'
        },
        dateTime: today().add(1, 'hour')
      }]
    },
    {
      date: today().add(2, 'day'),
      disabled: false,
      data: [{
        name: 'It is wednesday my dudes',
        description: 'First element of the day',
        dateTime: today().add(1, 'hour'),
        project: {
          id: 1,
          name: 'Agira'
        },
      }, {
        name: 'It is wednesday my dudes',
        description: 'Second element of the day',
        dateTime: today().add(2, 'hour'),
        project: {
          id: 1,
          name: 'Agira'
        },
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
        name: 'AGIRA BIEN',
        description: 'EQUIPE LT',
        dateTime: today().add(1, 'week').add(4, 'hour'),
        project: {
          id: 1,
          name: 'Agira'
        }
      }, {
        name: 'It is wednesday my dudes of week 2',
        description: 'Second element of the day',
        dateTime: today().add(1, 'week').add(8, 'hour')
      }]
    }
  ];
  const data = [staticData, staticDataWeek2];

  return (
    <MainPage title="Time entries">
      <WeekCalendar
        firstDay={currentFirstDay}
        disabledWeekEnd={true}
        hiddenButtons={false}
        onPanelChange={(id, start) => setCurrentFirstDay(start)}
        dateCellRender={(data) => {
          return (
            <div>
              {data.map(entry => {
                if (entry) {
                  return (
                    <TimeEntry entry={entry}/>
                  );
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

