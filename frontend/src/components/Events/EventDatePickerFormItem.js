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

import {DatePicker, Form} from 'antd';
import React from 'react';
import PropTypes from 'prop-types';

const EventDatePickerFormItem = ({label, name, isRequired, message, onChange, disabledDates}) => {
  return (<Form.Item
    label={label}
    name={name}
    rules={[{required: isRequired, message: message}]}
  >
    <DatePicker
      disabledDate={disabledDates}
      className="tk_DatePicker"
      onChange={onChange}
    />
  </Form.Item>);
};
EventDatePickerFormItem.propTypes = {
  label: PropTypes.string,
  name: PropTypes.string,
  isRequired: PropTypes.bool,
  message: PropTypes.string,
  onChange: PropTypes.func,
  disabledDates: PropTypes.func
};
export default EventDatePickerFormItem;