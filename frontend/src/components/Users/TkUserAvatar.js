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
import PropTypes from 'prop-types';
import _get from 'lodash/get';
import _isNull from 'lodash/isNull'
import {Avatar} from 'antd';
import UserOutlined from '@ant-design/icons/lib/icons/UserOutlined';
import './TkUserAvatar.less';

export const genNameColor = (name) => '#' + intToRGB(hashCode(name));
const hashCode = (str) => { // java String#hashCode
  let hash = 0;
  const chars = _get({"str": _isNull(str) ? undefined : str}, "str", "")
  for (let i = 0; i < chars.length; i++) {
    hash = chars.charCodeAt(i) + ((hash << 5) - hash);
  }
  return hash;
};
const intToRGB = (i) =>{
  const c = (i & 0x00FFFFFF)
      .toString(16)
      .toUpperCase();
  return '00000'.substring(0, 6 - c.length) + c;
};

const TkUserAvatar = ({ name,picture }) => {
  if (picture){
    // return given picture in Avatar
    return <Avatar src={picture}/>;
  }else if (name){
    // return first letter of the name in Avatar (google style with background color based on name value)
    return <Avatar className={'tk_user_avatar'} style={{
      backgroundColor: genNameColor(name),
      fontSize: 'large',
      fontFamily:'HelveticaNeue-Light,Helvetica Neue Light,Helvetica Neue,Helvetica, Arial,Lucida Grande, sans-serif'
    }}>{name.substr(0, 1)}</Avatar>;
  }else{
    // return an icon in Avatar
    return <Avatar icon={<UserOutlined />}/>;
  }
};

TkUserAvatar.propTypes = {
  name: PropTypes.string,
  picture: PropTypes.string
};

export default TkUserAvatar;