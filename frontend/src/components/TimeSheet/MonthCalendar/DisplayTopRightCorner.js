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

import PublicHolidayTag from './PublicHolidayTag';
import CompletedTag from './CompletedTag';
import {Button} from 'antd';
import {PlusOutlined} from '@ant-design/icons';
import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';

/*
* This representational component displays - Either:
* 1. Plus Button to add task when hours are not completed
* 2. PublicHoliday Text Tag
* 3. Completed tag when hours are completed
* At the top right corner in header of the Single date entry card
* */

const DisplayTopRightCorner = (props) => {
  const {isPublicHoliday, hoursCompleted, isDisabled, dateAsMoment, onClickPlusButton} = props;

  if(isPublicHoliday) return <PublicHolidayTag/>;
  if(hoursCompleted) return <CompletedTag/>;
  if(isDisabled) return <></>;
  return <Button
    shape="circle"
    icon={<PlusOutlined/>}
    onClick={(e) => {
      onClickPlusButton && onClickPlusButton(e, dateAsMoment);
      e.stopPropagation();
    }}>
  </Button>;
};

DisplayTopRightCorner.propTypes = {
  isPublicHoliday: PropTypes.bool,
  hoursCompleted: PropTypes.bool,
  isDisabled: PropTypes.bool,
  dateAsMoment:  PropTypes.instanceOf(moment),
  onClickPlusButton: PropTypes.func
};

export default DisplayTopRightCorner;
