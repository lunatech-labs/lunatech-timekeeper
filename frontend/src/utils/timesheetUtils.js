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

// Returns true if all timeUnits are disabled for a timesheet
import {getHalfDayDuration} from './configUtils';

// Returns true if all units are disabled, entryDuration is optional and is present only on timeEntry edition
export const isTimeSheetDisabled = (timeSheet, numberOfHoursForDay, entryDuration) => {
  const timeUnit = timeSheet && timeSheet.timeUnit;
  const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
  const halfDayDisabled = (timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY') ||
        (numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > getHalfDayDuration() : numberOfHoursForDay > getHalfDayDuration());
  const dayDisabled = numberOfHoursForDay && entryDuration ? (numberOfHoursForDay - entryDuration) > 0 : numberOfHoursForDay > 0;

  return hourDisabled && halfDayDisabled && dayDisabled;
};