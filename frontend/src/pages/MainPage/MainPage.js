import React, {useState} from 'react'
import { Layout } from "antd";
import './MainPage.less'
import SidebarLeft from "../../components/SidebarLeft/SidebarLeft";
import TopBar from "../../components/TopBar/TopBar";

const { Content, Footer } = Layout;

const MainPage = ({ children, title, ...rest }) => {
const [collapsed, toggle] = useState(false);
    return (
        <Layout>
            <SidebarLeft collapsed={collapsed} {...rest} />
            <Layout>
                <TopBar collapsed={collapsed} toggle={() => toggle(!collapsed)} />
                <Content className="mainContent">
                    {children}
                </Content>
                <Footer className="footerTk">Time Keeper v0.1 ©2020 Created by Lunatech</Footer>
            </Layout>
        </Layout>
    )
};

export default MainPage