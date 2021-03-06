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

import PropTypes from 'prop-types';
import moment from 'moment';
import React from 'react';
import DisplayTopRightCorner from './DisplayTopRightCorner';
import RenderMonthEntries from './RenderMonthEntries';

/*
* This is the single Day Card entry for a month calendar cells.
*
*  It has Top Right corner section where you can use Plus button to add task
*   or displays completed tag when hours are completed
*   or displays public tag when date of the single month card is public holiday
*   or disable the button when it is weekend
*
*   Render Time & Event Entries component
* */

const MonthDayCard = (props) => {
  const {dateAsMoment, userEventEntry, userTimeEntry, onClickPlusButton, isDayWithoutAnyEntries, isPublicHoliday, isDisabled, hoursCompleted} = props;

  const applyClass = (isDayWithoutAnyEntries && !isDisabled) ? 'tk_CardMonthCalendar_Body_With_Warn' : isDisabled ? 'tk_CardMonthCalendar_Body' : '';

  return (
    <div className={applyClass}>
      <DisplayTopRightCorner
        isPublicHoliday={isPublicHoliday}
        hoursCompleted={hoursCompleted}
        dateAsMoment={dateAsMoment}
        onClickPlusButton={onClickPlusButton}
        isDisabled={isDisabled}
      />
      <RenderMonthEntries
        userTimeEntry={userTimeEntry}
        userEventEntry={userEventEntry}
      />
    </div>
  );
};

MonthDayCard.propTypes = {
  dateAsMoment: PropTypes.instanceOf(moment),
  onClickPlusButton: PropTypes.func,
  userEventEntry: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string,
      description: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string,
      date: PropTypes.string
    })
  ).isRequired,
  userTimeEntry: PropTypes.shape({
    date: PropTypes.object,
    disabled: PropTypes.bool,
    data: PropTypes.any
  }).isRequired,
  isDayWithoutAnyEntries: PropTypes.bool,
  isPublicHoliday: PropTypes.bool,
  isDisabled: PropTypes.bool,
  hoursCompleted: PropTypes.bool
};

export default MonthDayCard;
