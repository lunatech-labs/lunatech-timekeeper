/*
 * Copyright 2020 Lunatech Labs
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

import {renderRangeWithYear, renderRange, weekRangeOfDate, totalHoursPerDay} from './momentUtils';
import moment from 'moment';

// eslint-disable-next-line
test('renderRangeWithYear a range year with different month', () => {
  const startDate = moment.utc('2020-01-22');
  const endDate = moment.utc('2020-02-01');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('22 Jan - 01 Feb 2020');
});

// eslint-disable-next-line
test('renderRangeWithYear a range year', () => {
  const startDate = moment.utc('2020-01-01');
  const endDate = moment.utc('2020-01-31');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('01 - 31 Jan 2020');
});

// eslint-disable-next-line
test('renderRangeWithYear a range with 2 different years', () => {
  const startDate = moment.utc('2019-12-22');
  const endDate = moment.utc('2020-02-01');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('22 Dec 2019 - 01 Feb 2020');
});

// eslint-disable-next-line
test('renderRangeWithYear should not crash if start is not valid', () => {
  const startDate = undefined;
  const endDate = moment.utc('2020-02-01');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('invalid startDate');
});

// eslint-disable-next-line
test('renderRangeWithYear should not crash if end is not valid', () => {
  const startDate = moment.utc('2020-02-01');
  const endDate = undefined;
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('invalid endDate');
});

// eslint-disable-next-line
test('renderRangeWithYear should not accept endDate that is before startDate', () => {
  const startDate = moment.utc('2020-05-25');
  const endDate = moment.utc('2020-02-01');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('invalid range, endDate must be after startDate');
});

// eslint-disable-next-line
test('renderRangeWithYear should accept if startDate and endDate are the same day', () => {
  const startDate = moment.utc('2020-05-25');
  const endDate = moment.utc('2020-05-25');
  const result = renderRangeWithYear(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('25 May 2020');
});

// eslint-disable-next-line
test('renderRange with a start and an end date', () => {
  const startDate = moment.utc('2020-02-01');
  const endDate = moment.utc('2020-05-25');
  const result = renderRange(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('01 Feb - 25 May');
});

// eslint-disable-next-line
test('renderRange with a start and an end date same month', () => {
  const startDate = moment.utc('2020-05-01');
  const endDate = moment.utc('2020-05-25');
  const result = renderRange(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('01 - 25 May');
});

// eslint-disable-next-line
test('renderRange with invalid start date', () => {
  const startDate = undefined;
  const endDate = moment.utc('2020-05-25');
  const result = renderRange(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('invalid startDate');
});

// eslint-disable-next-line
test('renderRange with invalid end date', () => {
  const startDate = moment.utc('2020-05-25');
  const endDate = undefined;
  const result = renderRange(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('invalid endDate');
});

// eslint-disable-next-line
test('renderRange with same day', () => {
  const startDate = moment.utc('2020-05-01');
  const endDate = moment.utc('2020-05-01');
  const result = renderRange(startDate, endDate);

  // eslint-disable-next-line
    expect(result).toBe('01 May');
});

// eslint-disable-next-line
test('weekRangeOfDate with a specified firstDay', () => {
  const firstDay = moment.utc('2020-07-23');
  const numberOfWeeks = 7;
  const result = weekRangeOfDate(firstDay, numberOfWeeks);

  // eslint-disable-next-line
    expect(result).toHaveLength(7);

  // eslint-disable-next-line
  expect(result[0].id).toBe(23);
  // eslint-disable-next-line
  expect(result[0].start.isSame(moment.utc('2020-06-04'))).toBe(true);
  // eslint-disable-next-line
  expect(result[0].end.format()).toBe('2020-06-06T23:59:59Z'); // saturday
  // eslint-disable-next-line
  expect(result[0].end.isSame(moment.utc('2020-06-06'),'year')).toBe(true);
  // eslint-disable-next-line
  expect(result[0].end.isSame(moment.utc('2020-06-06'),'month')).toBe(true);
  // eslint-disable-next-line
  expect(result[0].end.isSame(moment.utc('2020-06-06'),'day')).toBe(true);
});

// eslint-disable-next-line
test('weekRangeOfDate with a specified firstDay as monday', () => {
  const firstDay = moment.utc('2020-07-20'); // monday
  const numberOfWeeks = 7;
  const result = weekRangeOfDate(firstDay, numberOfWeeks);

  // eslint-disable-next-line
    expect(result).toHaveLength(7);

  //expect(result).toBe([]);

  // eslint-disable-next-line
  expect(result[0].id).toBe(23);
  // eslint-disable-next-line
  expect(result[0].start.isSame(moment.utc('2020-06-01'))).toBe(true); // monday
  //expect(result[0].end.isSame(moment.utc("2020-06-07"))).toBe(true); // saturday
  // eslint-disable-next-line
  expect(result[0].end.format()).toBe('2020-06-06T23:59:59Z'); // saturday
});

// eslint-disable-next-line
test('totalHoursPerDay should return 0 for no entries', () =>{
  const emptyEntries=[];
  const result = totalHoursPerDay(emptyEntries);
  // eslint-disable-next-line
  expect(result).toBe(0);
});

// eslint-disable-next-line
test('totalHoursPerDay should return 8 for some entries', () =>{
  const emptyEntries=[];
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T09:00:00Z'),
    endDateTime: moment.utc('2020-08-04T11:00:00Z'),
  });
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T11:00:00Z'),
    endDateTime: moment.utc('2020-08-04T13:00:00Z'),
  });
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T15:00:00Z'),
    endDateTime: moment.utc('2020-08-04T17:00:00Z'),
  });
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T17:00:00Z'),
    endDateTime: moment.utc('2020-08-04T19:00:00Z'),
  });
  const result = totalHoursPerDay(emptyEntries);
  // eslint-disable-next-line
  expect(result).toBe(8);
});

// eslint-disable-next-line
test('totalHoursPerDay should return 3 hours for 2 entries that overlaps each other by one hour', () =>{
  const emptyEntries=[];
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T09:00:00Z'),
    endDateTime: moment.utc('2020-08-04T11:00:00Z'),
  });
  emptyEntries.push({
    startDateTime: moment.utc('2020-08-04T10:00:00Z'),
    endDateTime: moment.utc('2020-08-04T11:00:00Z'),
  });
  const result = totalHoursPerDay(emptyEntries);
  // eslint-disable-next-line
  expect(result).toBe(3);
});