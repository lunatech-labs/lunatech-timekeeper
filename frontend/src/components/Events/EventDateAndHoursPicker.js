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
import {Col, Row} from 'antd';
import './EventDateAndHoursPicker.less';
import '../../components/Button/BtnGeneral.less';
import 'moment/locale/en-gb';
import PropTypes from 'prop-types';

export const EventDateAndHoursPicker = ({datePicker, hoursPicker, cssClass, style}) => {
  return (
    <div className={cssClass} style={style}>
      <Row gutter={16}>
        <Col span={12} order={1}>
          {datePicker}
        </Col>
        <Col span={12} order={2}>
          {hoursPicker}
        </Col>
      </Row>
    </div>
  );
};

EventDateAndHoursPicker.propTypes = {
  datePicker: PropTypes.element.isRequired,
  hoursPicker: PropTypes.element.isRequired,
  cssClass: PropTypes.string,
  style: PropTypes.object
};

export default EventDateAndHoursPicker;