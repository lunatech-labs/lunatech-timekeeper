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
import {UserOutlined, BankOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';
import './EvenTypeIcon.less';

const EventTypeIcon = ({iconName, text}) => {
  if(iconName === 'UserOutlined'){
    return (
      <div class="tk_EventType_InnerChoice"> 
        <UserOutlined />
        <p>{text}</p>
      </div>
    );
  }
  if(iconName === 'BankOutlined'){
    return (
      <div class="tk_EventType_InnerChoice"> 
        <BankOutlined />
        <p>{text}</p>
      </div>
    );
  }
};

EventTypeIcon.propTypes = {
  iconName: PropTypes.string,
  text: PropTypes.string
};

export default EventTypeIcon;