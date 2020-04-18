import React from 'react'
import {Layout} from "antd";
import './SidebarLeft.less'
import Logo from "../Logo/Logo";
import MenuSidebar from "../MenuSidebar/MenuSidebar";
import BtnLogout from "../BtnLogout/BtnLogout";

const { Sider } = Layout;

const SidebarLeft = ({ collapsed }) => {
    return (
        <Sider width={240} className="site-layout-background" trigger={null} collapsible collapsed={collapsed}>
            <Logo />
            <MenuSidebar/>
            <BtnLogout/>
        </Sider>
    )
};

export default SidebarLeft