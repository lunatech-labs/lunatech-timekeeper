import React from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from "../../components/TimeSheet/WeekCalendar";
import {Badge} from "antd";

const moment = require('moment');

const TimeEntriesPage = () => {
  const firstDayOfWeek = moment().startOf('week').add(1, 'day'); //TODO : Locale

  const staticData = [
    {
      date: firstDayOfWeek,
      disabled: true,
      data: [{
        name: "First Day of the week is a day off",
      }]
    },
    {
      date: firstDayOfWeek.clone().add(2, 'day'),
      disabled: false,
      data: [{
        name: "It is wednesday my dudes",
        description: "First element of the day",
        dateTime: firstDayOfWeek.clone().add(4, 'hour')
      }, {
        name: "It is wednesday my dudes",
        description: "Second element of the day",
        dateTime: firstDayOfWeek.clone().add(8, 'hour')
      }]
    }
  ];
  return (
    <MainPage title="Time entries">
      <WeekCalendar
        firstDay={firstDayOfWeek}
        disabledWeekEnd={true}
        dateCellRender={(data, date, disabled) => {
          return (
            <div>
              {data.map(entry => {
                return (
                  <div>
                    <Badge
                      status={(entry && entry.name) ? 'success' : 'error'}
                      text={(entry && entry.name) ? `name : ${entry.name} ${entry.dateTime && `(${entry.dateTime.format('hh:mm')})`}` : 'Nothing to render'}
                    />
                  </div>
                )
              })}
            </div>
          )
        }}
        days={staticData}
      />
    </MainPage>
  );
};

export default TimeEntriesPage;

