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


export const isTimeSheetDisabled = (timeSheet, date, numberOfHoursForDay, entryDuration) => {
  return isTimeUnitDisabled(timeSheet, numberOfHoursForDay, entryDuration) && isDateOutOfTimeSheetRange(timeSheet, date);
};

// Returns true if all units are disabled, entryDuration is optional and is present only on timeEntry edition
const isTimeUnitDisabled = (timeSheet, numberOfHoursForDay, entryDuration) => {
  const timeUnit = timeSheet && timeSheet.timeUnit;
  const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
  const halfDayDisabled = (timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY') ||
      (numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > getHalfDayDuration() : numberOfHoursForDay > getHalfDayDuration());
  const dayDisabled = numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > 0 : numberOfHoursForDay > 0;

  return hourDisabled && halfDayDisabled && dayDisabled;
}

// Returns true if the date is not in timesheets date range
const isDateOutOfTimeSheetRange = (timeSheet, date) => {
  if(timeSheet.expirationDate){
    return isDateOutOfTimeSheetRangeWithEndDate(timeSheet, date);
  }
  return isDateOutOfTimeSheetRangeWithOutEndDate(timeSheet, date);
}

// Returns true if the date is before the startDate with no endDate
const isDateOutOfTimeSheetRangeWithOutEndDate = (timeSheet, date) => {
  const startDate = moment(timeSheet.startDate).utc();
  return !(date.isAfter(startDate) || date.isSame(startDate));
}

// Returns true if the date is before the startDate or after the endDate
const isDateOutOfTimeSheetRangeWithEndDate = (timeSheet, date) => {
  const startDate = moment(timeSheet.startDate).utc();
  const endDate = moment(timeSheet.expirationDate).utc();
  return !date.isBetween(startDate, endDate,undefined, []);
}