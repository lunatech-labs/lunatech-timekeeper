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

import './WeekCalendar.less';
import UserEvent from '../../UserEvent/UserEvent';

const UserEventsForADay = ({eventEntries}) => {
  return eventEntries.map(userEventDay => {
    return <React.Fragment key={''}>
      <UserEvent userEvent={userEventDay}/>
    </React.Fragment>;
  });
};

UserEventsForADay.propTypes = {
  eventEntries:  PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      date: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
      eventUserDaysResponse: PropTypes.arrayOf(
        PropTypes.shape({
          name: PropTypes.string,
          description: PropTypes.string,
          startDateTime: PropTypes.string,
          endDateTime: PropTypes.string,
          date: PropTypes.string
        })
      ),
      eventType: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string,
      duration: PropTypes.string
    })
  )
};

export default UserEventsForADay;
