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
import {Link, withRouter} from 'react-router-dom';
import {Menu} from 'antd';
import './MenuSidebar.less';
import PropTypes from 'prop-types';
import {ClockCircleOutlined, FolderOpenOutlined, TeamOutlined, AppstoreOutlined, UserOutlined, CalendarOutlined} from '@ant-design/icons';
import {useKeycloak} from '@react-keycloak/web';

const MenuSidebar = (props) => {
  const [keycloak] = useKeycloak();

  const isAdmin = keycloak.hasRealmRole('admin');
  const {location} = props;
  const splitPathname = location.pathname.split('/').filter(i => i);
  const selectedKeys = splitPathname.length === 0 ? [] : ['/' + splitPathname[0]];

  return (
    <Menu id="tk_Menu" defaultSelectedKeys={['/home']} mode="inline" selectedKeys={selectedKeys} theme="dark">
      <Menu.Item className="tk_MenuItem" key="/home">
        <Link to="/home">
          <AppstoreOutlined />
          <span>Dashboard</span>
        </Link>
      </Menu.Item>
      <Menu.Item className="tk_MenuItem" key="/time_entries">
        <Link to="/time_entries">
          <ClockCircleOutlined />
          <span>Time entries</span>
        </Link>
      </Menu.Item>
      {isAdmin && <Menu.Item className="tk_MenuItem" key="/clients">
        <Link to="/clients">
          <UserOutlined />
          <span>Clients</span>
        </Link>
      </Menu.Item>}
      <Menu.Item className="tk_MenuItem" key="/projects">
        <Link to="/projects">
          <FolderOpenOutlined />
          <span>Projects</span>
        </Link>
      </Menu.Item>
      <Menu.Item className="tk_MenuItem" key="/users">
        <Link to="/users">
          <TeamOutlined />
          <span>Users</span>
        </Link>
      </Menu.Item>
      <Menu.Item className="tk_MenuItem" key="/events">
        <Link to="/events">
          <CalendarOutlined />
          <span>Events</span>
        </Link>
      </Menu.Item>
    </Menu>
  );
};

MenuSidebar.propTypes = {
  location: PropTypes.object.isRequired
};

export default withRouter(MenuSidebar);