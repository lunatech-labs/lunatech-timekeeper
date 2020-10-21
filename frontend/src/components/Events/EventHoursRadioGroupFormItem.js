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

import {Form, Radio} from 'antd';
import React from 'react';
import PropTypes from 'prop-types';
import _ from 'lodash';
import {Durations} from '../../utils/configUtils';

export const EventHoursRadioGroupValues = [Durations.HALF_DAY, Durations.DAY];

const EventHoursRadioGroupFormItem = ({label, name, isRequired, message, onChange, value}) => {
  return (
    <Form.Item
      label={label}
      name={name}
      rules={[{required: isRequired, message: message}]}
      value={value}
    >
      <Radio.Group onChange={onChange} className="tk_UserEvent_Radio_Button">
        {_.isEqual(value, Durations.DAY) ? <Radio value={Durations.DAY} checked={true}>Day</Radio> : <Radio value={Durations.DAY} checked={false}>Day</Radio>}
        {_.isEqual(value, Durations.HALF_DAY) ? <Radio value={Durations.HALF_DAY} checked={true}>Half day</Radio> : <Radio value={Durations.HALF_DAY} checked={false}>Half day</Radio>}
      </Radio.Group>
    </Form.Item>
  );
};

EventHoursRadioGroupFormItem.propTypes = {
  label: PropTypes.string,
  name: PropTypes.string,
  isRequired: PropTypes.bool,
  message: PropTypes.string,
  onChange: PropTypes.func,
  value: PropTypes.number
};

export default EventHoursRadioGroupFormItem;