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

import React, { Component } from 'react';
import { Descriptions } from 'antd';
import PropTypes from 'prop-types';
import './ShowUser.less';
import TkUserAvatar from './TkUserAvatar';

class ShowUser extends Component {

  render() {
    return (
      <Descriptions
        className="tk_Card"
        bordered
        column={{ xxl: 1, xl: 1, lg: 1, md: 1, sm: 1, xs: 1 }}>
        <Descriptions.Item label="Avatar"><TkUserAvatar picture={this.props.user.picture} name={this.props.user.name}/></Descriptions.Item>
        <Descriptions.Item label="Full name"> {this.props.user.name}</Descriptions.Item>
        <Descriptions.Item label="Email"> {this.props.user.email}</Descriptions.Item>
        <Descriptions.Item label="Profiles"> {this.props.user.profiles.join(', ')}</Descriptions.Item>
      </Descriptions>
    );
  }

}

ShowUser.propTypes = {
  user: PropTypes.object.isRequired
};

export default ShowUser;
