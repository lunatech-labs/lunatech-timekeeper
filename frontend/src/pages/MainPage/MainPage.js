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

import React, {useState} from 'react';
import {Alert, Layout, Typography} from 'antd';
import './MainPage.less';
import Breadcrumbs from '../../components/Breadcrumbs/Breadcrumbs';
import SidebarLeft from '../../components/SidebarLeft/SidebarLeft';
import TopBar from '../../components/TopBar/TopBar';
import PropTypes from 'prop-types';
import {useTimeKeeperAPI} from '../../utils/services';
import {UserProvider} from '../../context/UserContext';


const {Title} = Typography;

const {Content, Footer} = Layout;

const MainPage = ({title, children, actions, entityName, ...rest}) => {
  const {data, error, loading} = useTimeKeeperAPI('/api/users/me');

  const [collapsed, toggle] = useState(false);

  if (error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load user from Quarkus backend server'
          type='error'
        />
      </React.Fragment>
    );
  }

  if (loading) {
    return (
      <div>loading...</div>
    );
  }

  return (
    <Layout>
      <UserProvider currentUser={data}>
        <SidebarLeft collapsed={collapsed} {...rest} />
        <Layout id="tk_RightContent" className="site-layout">
          <TopBar collapsed={collapsed} toggle={() => toggle(!collapsed)}/>
          <Content id="tk_MainContent" className="mainContent">
            <div className="tk_MainContent_Header">
              <div className="tk_MainContent_HeaderLeft">
                <Breadcrumbs entityName={entityName}/>
                <Title id="title">{title}</Title>
              </div>
              <div>{actions}</div>
            </div>
            <div>
              {children}
            </div>
          </Content>
          <Footer className="tk_Footer">TimeKeeper ©2020 Lunatech</Footer>
        </Layout>
      </UserProvider>
    </Layout>
  );
};

MainPage.propTypes = {
  title: PropTypes.string,
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired,
  actions: PropTypes.object,
  entityName: PropTypes.string
};

export default MainPage;