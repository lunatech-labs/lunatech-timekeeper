import React from 'react';
import { Layout } from 'antd';
import './MainPage.less';
import SidebarLeft from '../../components/SidebarLeft/SidebarLeft';
import PropTypes from 'prop-types';

const { Header, Content, Footer } = Layout;

const MainPage = ({ children, ...rest }) => (
  <Layout>
    <SidebarLeft {...rest} />
    <Layout>
      <Header className="header">
      </Header>
      <Content className="mainContent">
        {children}
      </Content>
      <Footer className="footerTk">Time Keeper v0.1 ©2020 Created by Lunatech</Footer>
    </Layout>
  </Layout>
);

MainPage.propTypes = {
  children: PropTypes.object.isRequired
};

export default MainPage;