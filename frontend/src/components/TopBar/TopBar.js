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

import React, {useContext, useState} from 'react';
import {MenuUnfoldOutlined, MenuFoldOutlined, BellOutlined} from '@ant-design/icons';
import {Layout, Avatar, Badge} from 'antd';
import './TopBar.less';
import PropTypes from 'prop-types';
import OrganizationPictureFR from '../../img/organization_icon_fr.png';
import OrganizationPictureNL from '../../img/organization_icon_nl.png';
import HeaderProfile from './HeaderProfile';
import {UserContext} from '../../context/UserContext';

const { Header } = Layout;

const TopBar = ({ collapsed, toggle }) => {
  const [alert, setAlert] = useState(true);

  const {currentUser} = useContext(UserContext);
  const alertChangeState = () => {
    setAlert(!alert);
  };

  const displayOrganizationPicture = (email) => {
    if(email.includes('@lunatech.fr')){
      return OrganizationPictureFR;
    } else if(email.includes('@lunatech.nl')) {
      return OrganizationPictureNL;
    } else if(email.includes('@lunatech.com')) {
      return OrganizationPictureNL;
    } else {
      return '';
    }
  };

  return (
    <Header id="tk_Header" className="site-layout-background">
      <div className="tk_Header_Left">
        {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
          className: 'trigger',
          onClick: toggle,
        })}
      </div>
      <div className="tk_Header_Right">
        <div className="tk_Header_Organization">
          <Avatar shape="square"  src={displayOrganizationPicture(currentUser.email)} />
        </div>
        <div className="tk_Header_Notif">
          <Badge dot={alert} onClick={alertChangeState}>
            <BellOutlined />
          </Badge>
        </div>
        <HeaderProfile user={currentUser} />
      </div>
    </Header>
  );
};

TopBar.propTypes = {
  collapsed: PropTypes.bool.isRequired,
  toggle: PropTypes.func.isRequired
};

export default TopBar;