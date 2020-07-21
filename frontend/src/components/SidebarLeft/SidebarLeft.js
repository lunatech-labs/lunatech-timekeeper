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

import React  from 'react';
import {Layout} from 'antd';
import './SidebarLeft.less';
import Logo from '../Logo/Logo';
import MenuSidebar from '../MenuSidebar/MenuSidebar';
import PropTypes from 'prop-types';

const { Sider } = Layout;

const SidebarLeft = ({ collapsed }) => {
  return (

    <Sider width={240} id="tk_LeftSidebar" className="site-layout-background" trigger={null} collapsible collapsed={collapsed}>
      <Logo />
      <MenuSidebar/>
    </Sider>
  );
};

SidebarLeft.propTypes = {
  collapsed: PropTypes.bool.isRequired
};


export default SidebarLeft;