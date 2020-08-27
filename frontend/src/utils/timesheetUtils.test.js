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

import {isTimeSheetDisabled} from './timesheetUtils';
import moment from 'moment';

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a daily timesheet on an empty day on timeEntry creation', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'DAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'DAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16').utc(), 0);

  // eslint-disable-next-line
    expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for a daily timesheet on a not empty day on creation', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'DAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'DAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),1);

  // eslint-disable-next-line
    expect(result).toBe(true);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a daily timesheet on an empty day on edition', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'DAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'DAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),0, 4);

  // eslint-disable-next-line
    expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for a daily timesheet on a not empty day on edition', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'DAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'DAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),4, 1);

  // eslint-disable-next-line
    expect(result).toBe(true);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for an halfday timesheet on an empty day on timeEntry creation', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),0);

  // eslint-disable-next-line
    expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for an halfday timesheet with 5 hours on a day on creation', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    estartDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),5);

  // eslint-disable-next-line
    expect(result).toBe(true);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for an halfday timesheet on an empty day on edition', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),0, 4);

  // eslint-disable-next-line
    expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for an halfday timesheet on a not empty day on edition', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16'),6, 1);

  // eslint-disable-next-line
    expect(result).toBe(true);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for a date outside of timesheet range', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-15').utc(), 5);

  // eslint-disable-next-line
  expect(result).toBe(true);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a date the first day of timesheet range', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16').utc(), 0);

  // eslint-disable-next-line
  expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a date in timesheet range', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-26').utc(), 0);

  // eslint-disable-next-line
  expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a date the last day of timesheet range', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: '2020-04-16',
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-04-16').utc(), 0);

  // eslint-disable-next-line
  expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a date equals to the startDate with no endDate', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: null,
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-16').utc(), 0);

  // eslint-disable-next-line
  expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return false for a date after the startDate with no endDate', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: null,
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-19').utc(), 0);

  // eslint-disable-next-line
  expect(result).toBe(false);
});

// eslint-disable-next-line
test('isTimeSheetDisabled return true for a date before the startDate with no endDate', () => {
  const timesheetDay = {
    id: 1,
    timeUnit: 'HALFDAY',
    defaultIsBillable: false,
    startDate: '2020-03-16',
    expirationDate: null,
    maxDuration: 10,
    durationUnit: 'HALFDAY',
    project: {
      id: 1
    }
  };
  const result = isTimeSheetDisabled(timesheetDay,moment('2020-03-15').utc(), 5);

  // eslint-disable-next-line
  expect(result).toBe(true);
});