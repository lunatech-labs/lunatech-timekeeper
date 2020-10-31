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
import TimeEntry from '../../TimeEntry/TimeEntry';
import moment from 'moment';

const TimeEntriesForADay = (props) => {
  const {timeEntries, onClickEntryCard} = props;
  console.log('timeEntries: ' + timeEntries.date);
  return (timeEntries && timeEntries.timeEntriesForADay && timeEntries.timeEntriesForADay.data) ?
    timeEntries.timeEntriesForADay.data.map(entry => {
      return (
        <TimeEntry key={entry.id} entry={entry}
          onClick={mouseEvent => onClickEntryCard(mouseEvent, moment().utc(), entry.id)}
        />
      );
    }) : <></>;
};
export default TimeEntriesForADay;
