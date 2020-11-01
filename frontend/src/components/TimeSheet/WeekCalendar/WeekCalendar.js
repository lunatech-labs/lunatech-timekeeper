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
import PropTypes from 'prop-types';

import './WeekCalendar.less';
import {totalHoursPerDay, isPublicHoliday} from '../../../utils/momentUtils';
import WeekNavigationPanel from './WeekNavigationPanel';
import {getMaximumHoursPerDay} from '../../../utils/configUtils';
import {
  IsDayWithoutAnyEntries,
  IsDisabled,
  UserEventEntryData,
  UserTimeEntryData
} from '../CalendarUtils';
import WeekDayCard from './WeekDayCard';

const WeekCalendar = (props) => {
  const {
    onClickButton,
    onClickCard,
    onClickEntryCard,
    onPanelChange,
    onDateChange,
    publicHolidays,
    firstDay,
    userEvents,
    timeEntriesData,
    disabledWeekEnd
  } = props;

  const computeWeeklyTimeEntriesByDays = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => firstDay.clone().add(i, 'day'));
    return daysOfWeek.map(dateAsMoment => {
      return {
        date: dateAsMoment,
        timeEntriesForADay: UserTimeEntryData(dateAsMoment, timeEntriesData)
      };
    });
  };

  const weeklyTimeEntriesByDays = computeWeeklyTimeEntriesByDays();

  return (
    <div id="tk_WeekCalendar">
      <WeekNavigationPanel
        firstDay={firstDay}
        onDateChange={onDateChange}
        onPanelChange={onPanelChange}
      />
      <div id="tk_WeekCalendar_Body">
        { weeklyTimeEntriesByDays.map(timeEntries => {

          const dateAsMoment = timeEntries.date;
          const timeEntriesForADay = timeEntries.timeEntriesForADay.data;
          const eventEntries = UserEventEntryData(userEvents, dateAsMoment);

          const hoursCompleted = totalHoursPerDay(userEvents, dateAsMoment, timeEntriesForADay) >= getMaximumHoursPerDay();
          const isItPublicHoliday = isPublicHoliday(dateAsMoment, publicHolidays);
          const isDayWithoutAnyEntries = IsDayWithoutAnyEntries(dateAsMoment, timeEntriesForADay, eventEntries);
          const isDayDisabled = IsDisabled(dateAsMoment, timeEntriesData, disabledWeekEnd, publicHolidays);

          return (
            <React.Fragment key={`WeekDayCard-${timeEntries.date}`}>
              <WeekDayCard
                onClickCard={onClickCard}
                onClickButton={onClickButton}
                onClickEntryCard={onClickEntryCard}
                dateAsMoment={timeEntries.date}
                timeEntries={timeEntries}
                eventEntries={eventEntries}
                isItPublicHoliday={isItPublicHoliday}
                hoursCompleted={hoursCompleted}
                isDayDisabled={isDayDisabled}
                isDayWithoutAnyEntries={isDayWithoutAnyEntries}
              />
            </React.Fragment>
          );
        }
        )
        }
      </div>
    </div>
  );
};

WeekCalendar.propTypes = {
  onClickButton: PropTypes.func,
  onClickCard: PropTypes.func,
  onClickEntryCard: PropTypes.func,
  onPanelChange: PropTypes.func,
  onDateChange: PropTypes.func.isRequired,
  publicHolidays:  PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.string,
      localName: PropTypes.string,
      name: PropTypes.string,
      countryCode: PropTypes.string
    })
  ),
  firstDay: PropTypes.object.isRequired,
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
  ),
  timeEntriesData: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  disabledWeekEnd: PropTypes.bool,
};

export default WeekCalendar;
