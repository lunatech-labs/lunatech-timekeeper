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
import moment from 'moment';
import _ from 'lodash';
import en_GB from 'antd/lib/locale-provider/en_GB';
import 'moment/locale/en-gb';

import DatePanel from './DatePanel';
import MonthDayCard from './MonthDayCard';
import {isPublicHoliday, isWeekEnd, totalHoursPerDay} from '../../../utils/momentUtils';
import {getMaximumHoursPerDay} from '../../../utils/configUtils';
import './MonthCalendar.less';

const MonthCalendar = (props) => {
  const {contextDate, onDateChange, onClickPlusButton, timeEntriesData, disabledWeekEnd, onPanelChange, publicHolidays, userEvents } = props;

  const isDisabled = (dateAsMoment) => {
    const userTimeEntry = UserTimeEntryData(dateAsMoment);
    return userTimeEntry.disabled ? true :
      (isWeekEnd(dateAsMoment) && disabledWeekEnd) || isPublicHoliday(dateAsMoment, publicHolidays);
  };

  const UserTimeEntryData = (date) => {
    const data = timeEntriesData.find(entry => entry.date.isSame(moment(date).utc(), 'day'));
    return _.isUndefined(data) ? {} : data;
  };

  const UserEventEntryData = (userEvents, dateAsMoment) => {
    const data = userEvents.map(userEvent => userEvent.eventUserDaysResponse).flat()
      .filter(eventEntry => dateAsMoment.format('YYYY-MM-DD') === eventEntry.date);
    return _.isUndefined(data) ? [] : data;
  };

  const DayWithoutEntries = (currentDate, userTimeEntry, userEventEntry) => {
    const today = moment();
    const isCurrentDateBeforeToday = currentDate.isBefore(today , 'day');
    const isUserTimeEntryEmpty = _.isEmpty(userTimeEntry);
    const isUserEventEntryEmpty = _.isEmpty(userEventEntry);
    return(isCurrentDateBeforeToday && isUserTimeEntryEmpty && isUserEventEntryEmpty);
  };

  const MonthDayCellRender = (dateAsMoment) => {
    const userTimeEntry = UserTimeEntryData(dateAsMoment);
    const userEventEntry = UserEventEntryData(userEvents, dateAsMoment);
    const hoursCompleted = totalHoursPerDay(userEvents, dateAsMoment, userTimeEntry.data) >= getMaximumHoursPerDay();
    const isItPublicHoliday = isPublicHoliday(dateAsMoment, publicHolidays);
    const applyWarningClass = DayWithoutEntries(dateAsMoment, userTimeEntry, userEventEntry);
    const isDayDisabled = isDisabled(dateAsMoment);

    console.debug('MonthDayCellRender: [' + '\n'+
           ' Date: '+ dateAsMoment.format('DD-MM-YYYY') + '\n' +
           ' Hours completed on this day: ' + hoursCompleted + '\n' +
           ' Is It PublicHoliday: ' + isItPublicHoliday + '\n' +
           ' Mark this day with warning class: ' + applyWarningClass + '\n' +
           ' Is this day disabled (on Weekend or Public holiday): ' + isDayDisabled + '\n' +
           ' TimeEntry for this day: ' + JSON.stringify(userTimeEntry) + '\n' +
           ' EventEntry for this day: ' + JSON.stringify(userEventEntry) + '\n' + ' ]');

    return (
      <MonthDayCard
        dateAsMoment={dateAsMoment}
        userEventEntry={userEventEntry}
        userTimeEntry={userTimeEntry}
        onClickPlusButton={onClickPlusButton}
        applyWarningClass={applyWarningClass}
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
            return isDisabled(dateAsMoment);
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
