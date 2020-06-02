import React, {useEffect, useState} from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import {Badge} from 'antd';
import momentUtil from '../../utils/momentsUtil';

const {moment} = momentUtil();

const TimeEntriesPage = () => {
  const firstDayOfCurrentWeek = moment().startOf('week');
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
        client: {
          id: 1,
          name: 'Darva'
        },
        dateTime: today().add(4, 'hour')
      },{
        name: 'It is tuesday my dudes',
        description: 'Second element of the day',
        client: {
          id: 1,
          name: 'Google'
        },
        dateTime: today().add(2, 'hour')
      },{
        name: 'It is tuesday my dudes',
        description: 'Second element of the day',
        client: {
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
        dateTime: today().add(1, 'hour')
      }, {
        name: 'It is wednesday my dudes',
        description: 'Second element of the day',
        dateTime: today().add(2, 'hour')
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
        client: {
          id: 1,
          name: 'Darva'
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
                    <div key={`badge-entry-${entry.dateTime && entry.dateTime.format('yyyy-mm-dd-hh-mm')}`}>
                      <Badge
                        status={(entry && entry.name) ? 'success' : 'error'}
                        text={(entry && entry.name) ? `name : ${entry.name} ` : 'Nothing to render'}
                      />
                      <p>

                      </p>
                      <p>
                        {(entry && entry.client && entry.client.name) ? entry.client.name: 'No client'}
                      </p>

                      <p>
                        {entry.dateTime.format('hh:mm')}
                      </p>
                    </div>
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

