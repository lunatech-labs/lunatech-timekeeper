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

import moment from 'moment';
import _ from 'lodash';

// Render a range of date with the year
// same month, same year => 22 - 25 May 2020
// different months, same year => 22 Jan - 01 Feb 2020
export const renderRangeWithYear = (start, end) => {
  const panelFormatWithYear = 'DD MMM YYYY';
  const panelFormat = 'DD MMM';

  if(!start){
    return 'invalid startDate';
  }
  if(!end){
    return 'invalid endDate';
  }

  if(start.isAfter(end)){
    return 'invalid range, endDate must be after startDate';
  }

  if(start.isSame(end)){
    return start.format(panelFormatWithYear);
  }

  if ((start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // same month, same year => 22 - 25 May 2020
    return `${start.format('DD')} - ${end.format(panelFormatWithYear)}`;
  } else if ((!start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // different months, same year => 22 Jan - 01 Feb 2020
    return `${start.format(panelFormat)} - ${end.format(panelFormatWithYear)}`;
  } else {
    return `${start.format(panelFormatWithYear)} - ${end.format(panelFormatWithYear)}`;
  }
};

// Render a range of date without the year
export const renderRange = (start, end) => {
  const panelFormat = 'DD MMM';
  if(!start){
    return 'invalid startDate';
  }
  if(!end){
    return 'invalid endDate';
  }

  if(start.isSame(end)){
    return `${start.format(panelFormat)}`;
  }

  if (start.isSame(end, 'month')) {
    return `${start.format('DD')} - ${end.format(panelFormat)}`;
  } else {
    return `${start.format(panelFormat)} - ${end.format(panelFormat)}`;
  }
};

// Compute the week from the first day (or the start of the current week)
export const weekRangeOfDate = (firstDay, numberOfWeek) => {
  const startOfCurrentWeek = firstDay || moment.utc().startOf('week');
  return [...Array(numberOfWeek).keys()].map(i => {
    const toAdd = i - Math.trunc(numberOfWeek / 2);
    const start = startOfCurrentWeek.clone().add(toAdd, 'week');
    const end = start.clone().endOf('week');
    return {
      id: start.isoWeek(),
      start: start,
      end: end
    };
  });
};

// Compute userEvents Duration Per Day
const userEventsDurationPerDay = (userEvents, date) => {
  return _.sumBy(userEvents, function(userEvent){
    if(userEvent.eventUserDaysResponse) {
      return [...Array(userEvent.eventUserDaysResponse.length).keys()]
        .filter(i => date.format('YYYY-MM-DD') === userEvent.eventUserDaysResponse[i].date)
        .map(i => {
          const start = moment(userEvent.eventUserDaysResponse[i].startDateTime).utc();
          const end = moment(userEvent.eventUserDaysResponse[i].endDateTime).utc();
          const duration = moment.duration(end.diff(start));
          return duration.asHours();
        });
    }
  });
};

// Compute timeEntries Duration Per Day
const timeEntriesDuration = (timeEntries) => {
  if(timeEntries){
    return _.sumBy(timeEntries, function(entry){
      return entry.numberOfHours;
    });
  }
  return 0;
};

// Compute number of Hours Per Day timeEntries and userEvents
export const totalHoursPerDay = (userEvents, date, timeEntries) => {
  return Number(timeEntriesDuration(timeEntries)) + Number(userEventsDurationPerDay(userEvents, date));
};

// Returns true if the date is a publicHolidays
export const isPublicHoliday = (date, publicHolidays) => {
  if(date) {
    const res = _.find(publicHolidays, function(d){
      if(d.date){
        const formatted = date.format('YYYY-MM-DD');
        const isSameDate = formatted.localeCompare(d.date);
        return isSameDate === 0;
      }
      return false;
    });
    return _.isObject(res);
  }else{
    return false;
  }
};

// Returns true if the date is saturday or sunday
export const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;

//Returns false if the date is saturday or sunday
export const isNotWeekEnd = (date) => !isWeekEnd(date);

//Returns false if the date is a public holiday
export const isNotPublicHoliday = (date, publicHolidays) => !isPublicHoliday(date, publicHolidays);

// Returns the number of hours between two dates
export const computeNumberOfHours = (start, end) => {
  return moment.duration(end.diff(start)).asHours();
};

// Moment gives the month number with the index starting from 0
export const getIsoMonth = (m) => m.month() + 1;