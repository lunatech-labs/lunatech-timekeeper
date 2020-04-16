import React from 'react'
import {Layout} from "antd";
import './Container.scss'
import Logo from "../Logo/Logo";
import MenuSidebar from "../MenuSidebar/MenuSidebar";
import BtnLogout from "../BtnLogout/BtnLogout";

const { Sider: Sidebar } = Layout;

const Sidebar = ({ }) => {

    return (
        <Sidebar width={200} className="site-layout-background" collapsed={false}>
            <Logo />
            <MenuSidebar/>
            <BtnLogout/>
        </Sidebar>
    )
};

export default Sidebar