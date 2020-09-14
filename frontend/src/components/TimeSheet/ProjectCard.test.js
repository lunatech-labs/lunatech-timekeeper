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
import {makeItGrey} from "../../components/TimeSheet/ProjectCard";

// eslint-disable-next-line
test('When Now is before StartDate or Now is after EndDate and days-left = 0, should return class=tk_UnlimitedField', () => {

    const now = moment('2020-01-05'); // 05 Jan
    const startDate = moment('2020-01-06'); // 06 Jan
    const endDate = moment('2020-01-04'); // 04 Jan
    const daysLeft = 0;

    const result = makeItGrey(now, startDate, endDate, daysLeft);

    // eslint-disable-next-line
    expect(result).toBe('tk_UnlimitedField');
});

// eslint-disable-next-line
test('When Now is after StartDate or Now is before EndDate and days-left > 0, should return class=\'\'', () => {

    const now = moment('2020-01-05'); // 05 Jan
    const startDate = moment('2020-01-04'); // 04 Jan
    const endDate = moment('2020-01-06'); // 06 Jan
    const daysLeft = 1;

    const result = makeItGrey(now, startDate, endDate, daysLeft);

    // eslint-disable-next-line
    expect(result).toBe('');
});
