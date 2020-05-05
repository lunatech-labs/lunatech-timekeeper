import React, {useState} from 'react';
import {Layout, Typography} from 'antd';
import './MainPage.less';
import Breadcrumbs from '../../components/Breadcrumbs/Breadcrumbs';
import SidebarLeft from '../../components/SidebarLeft/SidebarLeft';
import TopBar from '../../components/TopBar/TopBar';
import PropTypes from 'prop-types';


const { Title } = Typography;

const { Content, Footer } = Layout;

const MainPage = ({ title, children, actions, ...rest }) => {
  const [collapsed, toggle] = useState(false);
  return (
    <Layout>
      <SidebarLeft collapsed={collapsed} {...rest} />
      <Layout className="site-layout">
        <TopBar collapsed={collapsed} toggle={() => toggle(!collapsed)} />
        <Content id="tk_MainContent" className="mainContent">
          <div className="">
            <Breadcrumbs />
            <div className="tk_Page_Actions">{actions}</div>
            <Title id="title">{title}</Title>
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
  actions: PropTypes.object
};

export default MainPage;