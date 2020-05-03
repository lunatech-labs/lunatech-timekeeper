import React, {useState} from 'react';
import {Layout, Typography} from 'antd';
import './MainPage.less';
import Breadcrumbs from '../../components/Breadcrumbs/Breadcrumbs';
import SidebarLeft from '../../components/SidebarLeft/SidebarLeft';
import TopBar from '../../components/TopBar/TopBar';
import PropTypes from 'prop-types';


const { Title } = Typography;

const { Content, Footer } = Layout;

const MainPage = ({ title, children, ...rest }) => {
  const [collapsed, toggle] = useState(false);
  return (
    <Layout>
      <SidebarLeft collapsed={collapsed} {...rest} />
      <Layout className="site-layout">
        <TopBar collapsed={collapsed} toggle={() => toggle(!collapsed)} />
        <Content id="tk_MainContent" className="mainContent">
          <div className="tk_TopPage">
            <Breadcrumbs />
            <Title>{title}</Title>
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
  children: PropTypes.object.isRequired
};

export default MainPage;