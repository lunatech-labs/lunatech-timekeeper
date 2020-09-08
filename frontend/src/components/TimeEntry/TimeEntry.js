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
import './TimeEntry.less';
import {Badge} from 'antd';
import {ClockCircleOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';
import moment from 'moment';

const computeSize = (nbHours) => {
  const minimumSize = 75;
  return minimumSize + (nbHours - 1) * 50;
};
const TimeEntry = ({entry, onClick}) => {
  const start = moment(entry.startDateTime).utc();
  const hours = entry.numberOfHours;
  const size = computeSize(hours);
  return (
    <div className="tk_TaskCard" style={{height: `${size}px`}}
      key={`badge-entry-${start && start.format('YYYY-MM-DD-HH-mm')}`} onClick={onClick}>
      <div>
        <Badge
          status={(entry && entry.comment) ? 'success' : 'error'}
          text={(entry && entry.comment) ? `${entry.comment}` : 'Nothing to render'}
        />
        <p>{(entry && entry.project && entry.project.name) ? entry.project.name : ''}</p>
      </div>
      <p><ClockCircleOutlined/>{hours}</p>
    </div>
  );
};

TimeEntry.propTypes = {
  entry: PropTypes.shape({
    comment: PropTypes.string.isRequired,
    project: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
    startDateTime: PropTypes.string.isRequired,
    numberOfHours: PropTypes.number.isRequired,
  }),
  onClick: PropTypes.func.isRequired
};

export default TimeEntry;