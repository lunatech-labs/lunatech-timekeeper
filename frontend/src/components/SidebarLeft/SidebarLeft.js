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