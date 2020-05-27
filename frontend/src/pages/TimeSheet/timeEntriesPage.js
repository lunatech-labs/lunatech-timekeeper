import React from 'react';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from "../../components/TimeSheet/WeekCalendar";
import {Badge} from "antd";

const moment = require('moment');

const TimeEntriesPage = () => {
  const firstDayOfWeek = moment().startOf('week').add(1, 'day');
  return (
    <MainPage title="Time entries">
      <WeekCalendar
        firstDay={firstDayOfWeek}
        dateCellRender={(data) => {
          return (
            <div><Badge status={(!!data && !!data.name) ? 'success' : 'error'} text={(data && data.name) || 'Nothing to render' } /></div>
          )
        }}
        days={[{
          moment:firstDayOfWeek,
          disabled: true,
          data: {
            name: "First Day of the week"
          }
        }]}
      />
    </MainPage>
  );
};

export default TimeEntriesPage;

