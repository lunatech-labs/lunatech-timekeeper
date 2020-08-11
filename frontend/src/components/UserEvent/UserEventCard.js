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
import './UserEventCard.less';
import PropTypes from 'prop-types';
import moment from 'moment';

const UserEventCard = ({event}) => {
  const start = moment(event.startDateTime);
  const end = moment(event.endDateTime);
  return (
    <div className="tk_UserEventCard"
      key={`tk-badge-userEvent-${start}`}>
      <div>
        <p>{(event && event.name) ? event.name : ''}</p>
        <p>{(event && event.description) ? event.description : ''}</p>
      </div>
      <p>{start.format('HH:mm')} - {end.format('HH:mm')}</p>
    </div>
  );
};
UserEventCard.propTypes = {
  event: PropTypes.shape({
    description: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    startDateTime: PropTypes.string.isRequired,
    endDateTime: PropTypes.string.isRequired,
  })
};

export default UserEventCard;