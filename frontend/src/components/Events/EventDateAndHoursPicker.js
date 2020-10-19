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
import {DatePicker, Col, Form, Row, Select} from 'antd';
import './EventDateAndHoursPicker.less';
import '../../components/Button/BtnGeneral.less';
import _ from 'lodash';
import 'moment/locale/en-gb';
import PropTypes from 'prop-types';

const EventDateAndHoursPicker = ({dateLabel, dateName, hoursLabel, hoursName, onDateChange, onHoursChange, marginTop}) => {
  const {Option} = Select;
  return (
    <div className="tk_DateAndHours" style={{marginTop: marginTop}}>
      <Row>
        <Col span={12} order={1}>
          <Form.Item
            label={dateLabel}
            name={dateName}
            rules={[{required: true}]}
          >
            <DatePicker
              className="tk_DatePicker"
              onChange={onDateChange}
            />
          </Form.Item>
        </Col>
        <Col span={12} order={2}>
          <Form.Item
            label={hoursLabel}
            name={hoursName}
            rules={[{required: true}]}
          >
            <Select onChange={onHoursChange}>
              {_.range(1, 9, 1).map(i => <Option key={`option-hour-${i}`} value={i} >{i}</Option>)};
            </Select>
          </Form.Item>
        </Col>
      </Row>
    </div>
  );
};

EventDateAndHoursPicker.propTypes = {
  dateLabel: PropTypes.string,
  dateName: PropTypes.string,
  hoursLabel: PropTypes.string,
  hoursName: PropTypes.string,
  onDateChange: PropTypes.func,
  onHoursChange: PropTypes.func,
  marginTop: PropTypes.number
};

export default EventDateAndHoursPicker;