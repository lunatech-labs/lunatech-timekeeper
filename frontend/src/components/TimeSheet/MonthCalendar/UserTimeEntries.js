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
import UserTimeEntry from './UserTimeEntry';

const UserTimeEntries = ({userTimeEntry}) => {
  return userTimeEntry.data ?
    userTimeEntry.data.map(entry =>
      <UserTimeEntry timeEntry={entry} key={`user-time-entry-${entry.id}`}/>
    ) : <></>;
};

UserTimeEntries.propTypes = {
  userTimeEntry: PropTypes.shape({
    date: PropTypes.object,
    disabled: PropTypes.bool,
    data: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.number,
        comment: PropTypes.string,
        project: PropTypes.any
      }).isRequired
    )
  })
};

export default UserTimeEntries;
