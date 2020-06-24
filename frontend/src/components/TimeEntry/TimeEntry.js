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

import React from 'react';
import './TimeEntry.less';
import {Badge} from 'antd';
import {ClockCircleOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';

const moment = require('moment');
const TimeEntry = ({entry}) => {
  const computeSize = (nbHours) => {
    const minimumSize = 75;

    return minimumSize + (nbHours - 1) * 50;
  };

  const start = moment(entry.startDateTime).utc();
  const end = moment(entry.endDateTime).utc();
  const duration = moment.duration(end.diff(start));
  const date = start.clone();
  date.set({
    hour: duration.asHours()
  });
  const hours = duration.asHours();
  const size = computeSize(hours);
  return (
    <div className="tk_TaskCard" style={{height: `${size}px`}}
      key={`badge-entry-${start && start.format('yyyy-mm-dd-hh-mm')}`}>
      <div>
        <Badge
          status={(entry && entry.comment) ? 'success' : 'error'}
          text={(entry && entry.comment) ? `${entry.comment}` : 'Nothing to render'}
        />
        <p>{(entry && entry.project && entry.project.name) ? entry.project.name : ''}</p>
      </div>
      <p><ClockCircleOutlined/>{date.format('hh:mm')}</p>
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
    endDateTime: PropTypes.string.isRequired,
  })
};

export default TimeEntry;