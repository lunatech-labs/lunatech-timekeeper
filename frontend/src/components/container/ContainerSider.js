import React from 'react'
import {Layout} from "antd";
import './Container.less'
import Logo from "../Logo/Logo";
import MenuSidebar from "../MenuSidebar/MenuSidebar";
import BtnLogout from "../BtnLogout/BtnLogout";

const { Sider } = Layout;

const ContainerSider = ({ }) => {

    return (
        <Sider width={200} className="site-layout-background" collapsed={false}>
            <Logo />
            <MenuSidebar/>
            <BtnLogout/>
        </Sider>
    )
};

export default ContainerSider