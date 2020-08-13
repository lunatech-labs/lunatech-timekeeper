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

import React, {useEffect, useState} from 'react';
import { useHistory } from 'react-router-dom';
import {Button, Select, Tag} from 'antd';
import CardWeekCalendar from '../Card/CardWeekCalendar';
import {
  LeftOutlined,
  PlusOutlined,
  RightOutlined,
  CheckOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
import PropTypes from 'prop-types';
import './WeekCalendar.less';
import {
  isWeekEnd,
  renderRange,
  renderRangeWithYear,
  weekRangeOfDate,
  totalHoursPerDay,
  isPublicHoliday,
} from '../../utils/momentUtils';
import moment from 'moment';
import _ from 'lodash';
import UserEvent from '../UserEvent/UserEvent';
import {getMaximumHoursPerDay} from '../../utils/configUtils';

const numberOfWeek = 15; // It's the number of weeks where we can navigate

const computeWeekRanges = (selectedDay) => {
  const weekRanges = weekRangeOfDate(selectedDay, numberOfWeek);
  return {
    weekNumber: selectedDay.isoWeek(),
    weekRange: weekRanges,
    weekRangeMap:  _.keyBy(weekRanges,'id')
  };
};

const WeekCalendar = (props) => {
  const publicHolidays = props.publicHolidays;
  const userEvents = props.userEvents;
  const [weekSelected, setWeekSelected] = useState(props.firstDay.isoWeek());
  const [weekRanges, setWeekRanges] = useState(computeWeekRanges(props.firstDay));
  const {onPanelChange} = props;
  const history = useHistory();

  useEffect(() => {
    const weekRange = _.get(weekRanges.weekRangeMap, weekSelected);
    if (weekRange && onPanelChange) {
      const {id, start, end} = weekRange;
      onPanelChange(id, start, end);
      if (weekRanges.weekNumber !== id) {
        setWeekRanges(computeWeekRanges(start));
        history.push('?year=2020&weekNumber=' + id);
      }
    }
  }, [weekSelected, onPanelChange, weekRanges, history]);

  const daysToData = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => props.firstDay.clone().add(i, 'day'));
    return daysOfWeek.map(dayOfWeek => {
      const day = props.days.find(d => d.date.isSame(moment(dayOfWeek).utc(), 'day'));
      return {
        date: dayOfWeek,
        day: day,
      };
    });
  };
  const dateFormat = props.dateFormat || 'Do';
  const headerDateFormat = props.headerDateFormat || 'ddd';
  const dataByDays = daysToData();

  const isDisabled = (item) => {
    if(item.day) {
      return (item.day.disabled || isPublicHoliday(item.day.date, publicHolidays));
    }else{
      return props.disabledWeekEnd && ( isWeekEnd(item.date) || isPublicHoliday(item.date, publicHolidays));
    }
  };

  const WeekNavigator = () => {
    const weekRangeIds = weekRanges.weekRange.map(weekRange => weekRange.id);
    const weekRange = _.get(weekRanges.weekRangeMap, weekSelected);
    const {start, end} = weekRange;
    const disableLeft = !weekRangeIds.includes(weekSelected - 1);
    const disableRight = !weekRangeIds.includes(weekSelected + 1);
    return (
      <div data-cy='weekNavigator'>
        <Button data-cy='btnWeekPrevious' data-cy-week={weekSelected - 1} icon={<LeftOutlined/>} disabled={disableLeft} onClick={() => setWeekSelected(weekSelected - 1)}/>
        <Button data-cy='btnWeekNext' data-cy-week={weekSelected + 1} icon={<RightOutlined/>} disabled={disableRight} onClick={() => setWeekSelected(weekSelected + 1)}/>
        <p>{renderRangeWithYear(start, end)}</p>
      </div>
    );
  };

  const WeekNavigatorSelect = () =>
    <Select data-cy='selectWeekNavigator' onChange={id => setWeekSelected(id)} defaultValue={0} value={weekSelected}>
      {weekRanges.weekRange.map(({id, start, end}) => {
        return (
          <Select.Option className={`${start.isSame(moment(), 'week') ? 'tk_CurrentWeekSelect' : ''}`}
            key={`date-range-${id}`} value={id} disabled={id === weekSelected}>
            {renderRange(start, end)}
          </Select.Option>
        );
      })}
    </Select>;

  const TopCardComponent = ({item, userEvents}) => {
    if(!isDisabled(item)){
      const timeEntries = item.day ? item.day.data : [];
      if(totalHoursPerDay(userEvents, item.date, timeEntries) >= getMaximumHoursPerDay()) {
        return <Tag className="tk_Tag_Completed"><CheckOutlined /> Completed</Tag>;
      }
      return <Button
        shape="circle"
        icon={<PlusOutlined/>}
        onClick={(e) => {
          props.onClickButton && props.onClickButton(e, item.date);
          e.stopPropagation();
        }}/>;
    }
    if(isPublicHoliday(item.date,publicHolidays)){
      return <Tag className="tk_Tag_Public_Holiday"><InfoCircleOutlined /> Public holiday</Tag>;
    }
    return '';
  };
  TopCardComponent.propTypes = {
    item: PropTypes.shape({
      date: PropTypes.object,
      day: PropTypes.shape({
        data: PropTypes.arrayOf(PropTypes.object)
      })
    }),
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
  return (
    <div id="tk_WeekCalendar">
      <div id="tk_WeekCalendar_Head">
        <WeekNavigator/>
        <div>
          <WeekNavigatorSelect/>
        </div>
      </div>
      <div id="tk_WeekCalendar_Body">
        {dataByDays.map((item, index) => {
          const renderDay = () => {
            if (item && item.day) {
              const {data, date, disabled} = item.day;
              return item && item.day && props.dateCellRender(data, date, disabled);
            }
          };
          const renderUserEvents = (userEvents) => {
            if(userEvents && item){
              if(!isWeekEnd(item.date) && !isPublicHoliday(item.date,publicHolidays)){
                return userEvents.map(userEvent => {
                  return userEvent.eventUserDaysResponse.map(userEventDay => {
                    if(userEventDay.date === item.date.format('YYYY-MM-DD')){
                      return <UserEvent userEvent={userEventDay}/>;
                    }
                  });
                });
              }
            }
          };
          const isToday = (day) => {
            return moment().isSame(day, 'day');
          };
          return (
            <div className="tk_WeekCalendar_Day" key={`day-card-${index}`}>
              <p>{item.date.format(headerDateFormat)}</p>
              <CardWeekCalendar onClick={(e) => props.onClickCard && props.onClickCard(e, item.date)}>
                <div className="tk_CardWeekCalendar_Head">
                  <p className={isToday(moment(item.date)) ? 'tk_CurrentDay' : ''}>{item.date.format(dateFormat)}</p>
                  <TopCardComponent item={item} userEvents={userEvents} />
                </div>
                <div className={props.warningCardPredicate && props.warningCardPredicate(item.date, item.day) ?
                  'tk_CardWeekCalendar_Body tk_CardWeekCalendar_Body_With_Warn' : 'tk_CardWeekCalendar_Body'} disabled={isDisabled(item)}>
                  {renderUserEvents(userEvents)}
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