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
import moment from 'moment';
import {computeNumberOfHours} from '../../utils/momentUtils';
import {Badge} from 'antd';
import './UserEvent.less';
import PropTypes from 'prop-types';
import {ClockCircleOutlined} from '@ant-design/icons';

const computeSize = (nbHours) => {
  const minimumSize = 75;
  return minimumSize + (nbHours - 1) * 50;
};
const UserEvent = ({userEvent}) => {
  const start = moment(userEvent.startDateTime).utc();
  const end = moment(userEvent.endDateTime).utc();
  const date = start.clone();
  const hours = computeNumberOfHours(start, end);
  date.set({
    hour: hours
  });
  const size = computeSize(hours);
  return (
    <div className="tk_UserEvent" style={{height: `${size}px`}}
      key={`badge-entry-${start && start.format('yyyy-mm-dd-hh-mm')}`}>
      <div>
        <Badge
          status={(userEvent && userEvent.name) ? 'success' : 'error'}
          text={(userEvent && userEvent.name) ? `${userEvent.name}` : 'Nothing to render'}
        />
        <p>{(userEvent && userEvent.description && userEvent.description) ? userEvent.description : ''}</p>
      </div>
      <p><ClockCircleOutlined/>{date.format('hh:mm')}</p>
    </div>
  );
};
UserEvent.propTypes = {
  userEvent: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      date: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string
    })
  ).isRequired
};

export default UserEvent;