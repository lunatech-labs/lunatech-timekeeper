/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import {Calendar, ConfigProvider} from 'antd';
import PropTypes from 'prop-types';
import en_GB from 'antd/lib/locale-provider/en_GB';
import 'moment/locale/en-gb';

import DatePanel from './DatePanel';
import MonthDayCard from './MonthDayCard';
import {isPublicHoliday, totalHoursPerDay} from '../../../utils/momentUtils';
import {getMaximumHoursPerDay} from '../../../utils/configUtils';
import './MonthCalendar.less';
import {
  UserTimeEntryData,
  IsDisabled,
  UserEventEntryData,
  IsDayWithoutAnyEntries
} from '../CalendarUtils';

/*
* This is representational component used to displays time and event entries
*
* */

const MonthCalendar = (props) => {
  const {contextDate, onDateChange, onClickPlusButton, timeEntriesData, disabledWeekEnd, onPanelChange, publicHolidays, userEvents } = props;

  const MonthDayCellRender = (dateAsMoment) => {
    const userTimeEntry = UserTimeEntryData(dateAsMoment, timeEntriesData);
    const userEventEntry = UserEventEntryData(userEvents, dateAsMoment);
    const hoursCompleted = totalHoursPerDay(userEvents, dateAsMoment, userTimeEntry.data) >= getMaximumHoursPerDay();
    const isItPublicHoliday = isPublicHoliday(dateAsMoment, publicHolidays);
    const isDayWithoutAnyEntries = IsDayWithoutAnyEntries(dateAsMoment, userTimeEntry, userEventEntry);
    const isDayDisabled = IsDisabled(dateAsMoment, timeEntriesData, disabledWeekEnd, publicHolidays);

    return (
      <MonthDayCard
        dateAsMoment={dateAsMoment}
        userEventEntry={userEventEntry}
        userTimeEntry={userTimeEntry}
        onClickPlusButton={onClickPlusButton}
        isDayWithoutAnyEntries={isDayWithoutAnyEntries}
        isPublicHoliday={isItPublicHoliday}
        isDisabled={isDayDisabled}
        hoursCompleted={hoursCompleted}
      />
    );
  };

  return (
    <div id="tk_MonthCalendar">
      <ConfigProvider locale={en_GB}>
        <Calendar
          value={contextDate}
          onChange={onDateChange}
          headerRender={render => {
            return <DatePanel dateAsMoment={render.value} onChange={render.onChange} onPanelChange={onPanelChange} />;
          }}
          disabledDate={dateAsMoment => {
            return IsDisabled(dateAsMoment, timeEntriesData, disabledWeekEnd, publicHolidays);
          }}
          dateCellRender={dateAsMoment => {
            return MonthDayCellRender(dateAsMoment);
          }}
        />
      </ConfigProvider>
    </div>
  );
};

MonthCalendar.propTypes = {
  contextDate: PropTypes.object.isRequired,
  onDateChange: PropTypes.func.isRequired,
  disabledWeekEnd: PropTypes.bool,
  timeEntriesData: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  onClickPlusButton: PropTypes.func, // (event, moment) => void
  onPanelChange: PropTypes.func, // (date) => void
  publicHolidays:  PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.string,
      localName: PropTypes.string,
      name: PropTypes.string,
      countryCode: PropTypes.string
    })
  ),
  userEvents: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      date: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
      eventUserDaysResponse: PropTypes.arrayOf(
        PropTypes.shape({
          name: PropTypes.string,
          description: PropTypes.string,
          startDateTime: PropTypes.string,
          endDateTime: PropTypes.string,
          date: PropTypes.string
        })
      ),
      eventType: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string,
      duration: PropTypes.string
    })
  )
};

export default MonthCalendar;
