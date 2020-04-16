import React from 'react'
import {Layout} from "antd";
import './SidebarLeft.less'
import Logo from "../Logo/Logo";
import MenuSidebar from "../MenuSidebar/MenuSidebar";
import BtnLogout from "../BtnLogout/BtnLogout";

const { Sider: SidebarLeft } = Layout;

const Sidebar = () => {
    return (
        <SidebarLeft width={200} className="site-layout-background" collapsed={false}>
            <Logo />
            <MenuSidebar/>
            <BtnLogout/>
        </SidebarLeft>
    )
};

export default Sidebar