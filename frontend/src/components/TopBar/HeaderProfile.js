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
import {Menu, Dropdown, Button} from 'antd';
import './HeaderProfile.less';
import {useKeycloak} from '@react-keycloak/web';
import PropTypes from 'prop-types';
import TkUserAvatar from '../Users/TkUserAvatar';

const HeaderProfile = ({ user }) => {

  const displayProfile = (profiles) => {
    if(profiles.includes('ADMIN')){
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
          <Button id="logoutBtn" type="link" onClick={() => keycloak.logout()}>Logout</Button>
        )}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className="tk_Header_Profile">
      <Dropdown overlay={menu}>
        <Button id="avatarBtn" type="link" onClick={e => e.preventDefault()}>
          <TkUserAvatar picture={user.picture} name={user.name}/>
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