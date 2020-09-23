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

import moment from 'moment';
import {filteredProjects} from './timesheetUtils';

// this is dummy TimeSheet object array containing only start and end dates
const timeSheets = [
  {
    'id': 1,
    'expirationDate': '2020-09-15',
    'startDate': '2020-09-01'
  },
  {
    'id': 2,
    'expirationDate': null,
    'startDate': '2020-09-01'
  },
  {
    'id': 3,
    'expirationDate': null,
    'startDate': null
  },
  {
    'id': 4,
    'expirationDate': null,
    'startDate': '2020-09-10'
  }
];


// eslint-disable-next-line
test('When Given Date is SameOrAfter the StartDate And SameOrBefore the EndDate', () => {
  const expectedResult = JSON.stringify([
    {
      'id': 1,
      'expirationDate': '2020-09-15',
      'startDate': '2020-09-01'
    },
    {
      'id': 2,
      'expirationDate': null,
      'startDate': '2020-09-01'
    },
    {
      'id': 3,
      'expirationDate': null,
      'startDate': null
    }]);
  const givenDate = moment('2020-09-05');
  const result = filteredProjects(timeSheets, givenDate);

  // eslint-disable-next-line
    expect(JSON.stringify(result)).toBe(expectedResult);
});

// eslint-disable-next-line
test('When Given Date is SameOrAfter the StartDate and Expiration date is null', () => {

  const expectedResult = JSON.stringify(timeSheets);

  const givenDate = moment('2020-09-10');
  const result = filteredProjects(timeSheets, givenDate);

  // eslint-disable-next-line
    expect(JSON.stringify(result)).toBe(expectedResult);
});


// eslint-disable-next-line
test('When Given Date is Any But the StartDate and Expiration dates are null', () => {

  const expectedResult = JSON.stringify([{'id':3,'expirationDate':null,'startDate':null}]);

  const givenDate = moment('2020-08-01');
  const result = filteredProjects(timeSheets, givenDate);

  // eslint-disable-next-line
    expect(JSON.stringify(result)).toBe(expectedResult);
});