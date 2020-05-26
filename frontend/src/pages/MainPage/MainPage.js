import React, {useState} from 'react';
import {Alert, Layout, Typography} from 'antd';
import './MainPage.less';
import Breadcrumbs from '../../components/Breadcrumbs/Breadcrumbs';
import SidebarLeft from '../../components/SidebarLeft/SidebarLeft';
import TopBar from '../../components/TopBar/TopBar';
import PropTypes from 'prop-types';
import {useTimeKeeperAPI} from '../../utils/services';


const { Title } = Typography;

const { Content, Footer } = Layout;

const MainPage = ({ title, children, actions, entityName, ...rest }) => {
  const { data, error, loading } = useTimeKeeperAPI('/api/users/me');


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
      <SidebarLeft collapsed={collapsed} {...rest} />
      <Layout id="tk_RightContent" className="site-layout">
        <TopBar collapsed={collapsed} toggle={() => toggle(!collapsed)} user={data} />
        <Content id="tk_MainContent" className="mainContent">
          <div className="tk_MainContent_Header">
            <div className="tk_MainContent_HeaderLeft">
              <Breadcrumbs entityName={entityName} />
              <Title id="title">{title}</Title>
            </div>
            <div>{actions}</div>
          </div>
          <div>
            {children}
          </div>
        </Content>
        <Footer className="tk_Footer">Time Keeper v0.1 ©2020 Created by Lunatech</Footer>
      </Layout>
    </Layout>
  );
};

MainPage.propTypes = {
  title: PropTypes.string,
  children: PropTypes.object.isRequired,
  actions: PropTypes.object,
  entityName: PropTypes.string
};

export default MainPage;