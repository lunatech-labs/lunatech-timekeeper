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
import PropTypes from 'prop-types';

import TimeEntry from '../../TimeEntry/TimeEntry';
import {nonEmpty} from '../../../utils/jsFunctionUtils';

const TimeEntriesForADay = (props) => {
  const {timeEntries, onClickEntryCard} = props;

  const checkIfDataExists = nonEmpty(timeEntries)
      && nonEmpty(timeEntries.timeEntriesForADay)
      && nonEmpty(timeEntries.timeEntriesForADay.data);

  return (checkIfDataExists) ?
    timeEntries.timeEntriesForADay.data.map(entry => {
      return (
        <TimeEntry key={entry.id} entry={entry}
          onClick={ mouseEvent =>
            onClickEntryCard(mouseEvent, timeEntries.date, entry.id)
          }
        />
      );
    }) : <></>;
};

TimeEntriesForADay.propTypes = {
  timeEntries: PropTypes.object,
  onClickEntryCard: PropTypes.func
};
export default TimeEntriesForADay;
