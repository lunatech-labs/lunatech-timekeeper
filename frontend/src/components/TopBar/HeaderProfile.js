/*
 * Copyright 2020 Lunatech Labs
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
import {Avatar, Menu, Dropdown, Button} from 'antd';
import './HeaderProfile.less';
import {useKeycloak} from '@react-keycloak/web';
import PropTypes from 'prop-types';

const HeaderProfile = ({ user }) => {

  const displayProfile = (profiles) => {
    if(profiles.includes('Admin')){
      return 'Admin';
    } else {
      return 'User';
    }
  };

  const { keycloak } = useKeycloak();

  const menu = (
    <Menu>
      <Menu.Item key="1">
        {!!keycloak.authenticated && (
          <Button type="link" onClick={() => keycloak.logout()}>Logout</Button>
        )}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className="tk_Header_Profile">
      <Dropdown overlay={menu}>
        <Button type="link" onClick={e => e.preventDefault()}>
          <Avatar src={user.picture} />
          <p>{user.name}<br/><span>{displayProfile(user.profiles)}</span></p>
        </Button>
      </Dropdown>
    </div>
  );
};

HeaderProfile.propTypes = {
  user: PropTypes.object.isRequired
};

export default HeaderProfile;