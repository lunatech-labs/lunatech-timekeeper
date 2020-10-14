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
import './EventMemberTag.less';
import PropTypes from 'prop-types';
import momentPropTypes from 'react-moment-proptypes';
import {getBusinessDays} from '../../utils/momentUtils';
import _ from 'lodash';
import moment from 'moment';

const EventDuration = ({startDate, startDateHours, endDate, endDateHours, locale}) => {

  const computeDuration = (startDate, startDateHours, endDate, endDateHours, locale) => {
    //Single day logic
    if(!_.isNull(startDate) && startDateHours > 0 && (_.isNull(endDate) || endDateHours < 1)) {
      const start = moment(startDate);
      const days = getBusinessDays(start, start, locale);
      const day = days
        .filter(d => d.startOf('day').isSame(start, 'day'));
      return day.length === 1 ? startDateHours : 0;
    }

    if(!_.isNull(startDate) && startDateHours > 0 && !_.isNull(endDate) && endDateHours > 0) {
      const start = moment(startDate).utc(true);
      const end = moment(endDate).utc(true);
      const businessDays = getBusinessDays(start, end, locale)
        .map(m => m.utc(true));

      const hoursAtStart = businessDays
        .filter(d => d.startOf('day').isSame(start, 'day'))
        .length === 1 ? startDateHours : 0;
      const hoursAtEnd = businessDays
        .filter(d => d.startOf('day').isSame(end, 'day'))
        .length === 1 ? endDateHours : 0;
      const hoursBetween = businessDays
        .filter(d => !d.startOf('day').isSame(start, 'day'))
        .filter(d => !d.startOf('day').isSame(end, 'day'))
        .length * 8;

      return hoursAtStart + hoursBetween + hoursAtEnd;
    }
    return -1;
  };

  const duration = computeDuration(startDate, startDateHours, endDate, endDateHours, locale);
  if(duration > 0) {
    return (
      <React.Fragment>
        <div>Duration of your event:</div>
        <div>{duration}</div>
      </React.Fragment>
    );
  } else {
    return (<div/>);
  }
};

EventDuration.propTypes = {
  startDate: momentPropTypes.momentObj,
  startDateHours: PropTypes.number,
  endDate: momentPropTypes.momentObj,
  endDateHours: PropTypes.number,
  locale: PropTypes.string.isRequired
};

export default EventDuration;