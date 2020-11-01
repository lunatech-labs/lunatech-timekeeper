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
import {isPublicHoliday, isWeekEnd,} from '../../../utils/momentUtils';
import moment from 'moment';
import CardWeekCalendar from '../../Card/CardWeekCalendar';
import WeekNavigationPanel from './WeekNavigationPanel';

const WeekCalendar = (props) => {
  const {publicHolidays, userEvents, firstDay, onDateChange, onPanelChange, timeEntriesData, onClickEntryCard, disabledWeekEnd, onClickButton, onClickCard} = props;

  const weekCalendarDataByDays = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => firstDay.clone().add(i, 'day'));
    return daysOfWeek.map(dayOfWeek => {
      const timeEntry = timeEntriesData.find(d => d.date.isSame(moment(dayOfWeek).utc(), 'day'));
      return {
        date: dayOfWeek,
        day: timeEntry,
      };
    });
  };

  const dataByDays = weekCalendarDataByDays();

  return (
    <div id="tk_WeekCalendar">
      <WeekNavigationPanel
        firstDay={firstDay}
        onDateChange={onDateChange}
        onPanelChange={onPanelChange}
      />
      <div id="tk_WeekCalendar_Body">
        {dataByDays.map((item, index) => {
          const renderDay = () => {
            if (item && item.day) {
              const {data, date, disabled} = item.day;
              return item && item.day && props.dateCellRender(data, date, disabled);
            }
          };

          const isToday = (day) => {
            return moment().isSame(day, 'day');
          };
          return (
            <div className="tk_WeekCalendar_Day" key={`day-card-${index}`}>
              <p>{item.date.format('ddd')}</p>
              <CardWeekCalendar onClick={(e) => props.onClickCard && props.onClickCard(e, item.date)}>
                <div className="tk_CardWeekCalendar_Head">
                  <p className={isToday(moment(item.date)) ? 'tk_CurrentDay' : ''}>{item.date.format('Do')}</p>
                </div>
                <div className={props.warningCardPredicate && props.warningCardPredicate(item.date, item.day) ?
                  'tk_CardWeekCalendar_Body tk_CardWeekCalendar_Body_With_Warn' : 'tk_CardWeekCalendar_Body'} disabled={false}>
                  {renderDay()}
                </div>
              </CardWeekCalendar>
            </div>
          );
        })}
      </div>
    </div>
  );
};

WeekCalendar.propTypes = {
  dateCellRender: PropTypes.func.isRequired, //(data, date, disabled) => node
  dateFormat: PropTypes.string,
  headerDateFormat: PropTypes.string,
  disabledWeekEnd: PropTypes.bool,
  onDateChange: PropTypes.func.isRequired,
  firstDay: PropTypes.object.isRequired,
  days: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  hiddenButtons: PropTypes.bool,
  onClickButton: PropTypes.func, // (event, moment) => void
  onPanelChange: PropTypes.func, // (id, start, end) => void
  onClickCard: PropTypes.func, // (event, moment) => void
  warningCardPredicate : PropTypes.func, // (date, day) => bool
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

export default WeekCalendar;