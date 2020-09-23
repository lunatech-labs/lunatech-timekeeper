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

import {getHalfDayDuration} from './configUtils';
import moment from 'moment';

// Filter the list based on start date end date and Given date
export const filteredProjects = (timeSheets, givenDate) => {
  return timeSheets.filter(timesheet =>
    (null == timesheet.startDate && null == timesheet.expirationDate) ||
      (moment(timesheet.startDate).isSameOrBefore(givenDate) && null == timesheet.expirationDate) ||
      (null == timesheet.startDate && moment(timesheet.expirationDate).isSameOrAfter(givenDate)) ||
      (moment(timesheet.startDate).isSameOrBefore(givenDate) && moment(timesheet.expirationDate).isSameOrAfter(givenDate))
  );
};

// Returns true if the timeUnits are disabled or if the date is out of range
export const isTimeSheetDisabled = (timeSheet, date, numberOfHoursForDay, entryDuration) => {
  if(timeSheet.leftOver === 0) return true; // days left equals 0
  return isTimeUnitDisabled(timeSheet, numberOfHoursForDay, entryDuration) || isDateOutOfTimeSheetRange(timeSheet, date);
};

// Returns true if all units are disabled, entryDuration is optional and is present only on timeEntry edition
const isTimeUnitDisabled = (timeSheet, numberOfHoursForDay, entryDuration) => {
  const timeUnit = timeSheet && timeSheet.timeUnit;
  const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
  const halfDayDisabled = (timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY') ||
      (numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > getHalfDayDuration() : numberOfHoursForDay > getHalfDayDuration());
  const dayDisabled = numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > 0 : numberOfHoursForDay > 0;

  return hourDisabled && halfDayDisabled && dayDisabled;
};

// Returns true if the date is not in timesheets date range
const isDateOutOfTimeSheetRange = (timeSheet, date) => {
  if(timeSheet.expirationDate){
    return isDateOutOfTimeSheetRangeWithEndDate(timeSheet, date);
  }
  return isDateOutOfTimeSheetRangeWithOutEndDate(timeSheet, date);
};

// Returns true if the date is before the startDate with no endDate
const isDateOutOfTimeSheetRangeWithOutEndDate = (timeSheet, date) => {
  const startDate = moment(timeSheet.startDate);
  const dateFormatted = moment(date.format('YYYY-MM-DD'));
  return !(dateFormatted.isSameOrAfter(startDate));
};

// Returns true if the date is before the startDate or after the endDate
const isDateOutOfTimeSheetRangeWithEndDate = (timeSheet, date) => {
  const startDate = moment(timeSheet.startDate);
  const endDate = moment(timeSheet.expirationDate);
  const dateFormatted = moment(date.format('YYYY-MM-DD'));
  return !dateFormatted.isBetween(startDate, endDate,undefined, []);
};