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
import SelectMonth from './SelectMonth';
import SelectYear from './SelectYear';
import MonthNavigator from './MonthNavigator';


/*
* This component renders custom header in a panel.
*
* e.g. When you select Month (from: RadioButton like component in UI for switching between Week and Month), then currently it will render two dropdowns:
* 1. Selecting month
* 2. Selecting year
* */

const DatePanel = ({dateAsMoment, onChange, onPanelChange}) => {
  const onChangeCustom = (date) => {
    onChange(date);
    onPanelChange(date);
  };
  return (
    <div id="tk_MonthCalendar_Head">
      <MonthNavigator value={dateAsMoment} onChange={onChangeCustom}/>
      <div>
        <SelectMonth value={dateAsMoment} onChange={onChangeCustom}/>
        <SelectYear value={dateAsMoment} onChange={onChangeCustom} />
      </div>
    </div>);
};
DatePanel.propTypes = {
  dateAsMoment: PropTypes.instanceOf(moment).isRequired,
  onChange: PropTypes.func.isRequired,
  onPanelChange: PropTypes.func.isRequired
};

export default DatePanel;
