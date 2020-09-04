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
import {Form, Select} from 'antd';
import PropTypes from 'prop-types';
const _ = require('lodash');

const {Option} = Select;

const SelectHoursComponent = ({numberOfHoursForDay, entryDuration}) => {
  const hourDisabled = (hour) => {
    if(entryDuration){
      return parseInt(hour) + (parseInt(numberOfHoursForDay) - entryDuration) > 8;
    }
    return parseInt(hour) + parseInt(numberOfHoursForDay) > 8;
  };
  return (
    <Form.Item name="numberOfHours" label="Number of hours:" rules={[{required: true}]}>
      <Select
        showSearch
      >
        {_.range(1, 9, 1).map(i => <Option key={`option-hour-${i}`} disabled={hourDisabled(i)} value={i} >{i}</Option>)};
      </Select>
    </Form.Item>
  );
};
SelectHoursComponent.propTypes = {
  numberOfHoursForDay: PropTypes.array.isRequired,
  entryDuration: PropTypes.number,
};

export default SelectHoursComponent;