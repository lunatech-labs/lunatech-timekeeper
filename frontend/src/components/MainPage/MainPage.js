import React from 'react'
import { Layout } from "antd";
import './MainPage.scss'
import SidebarLeft from "../SidebarLeft/SidebarLeft";

const { Header, Content, Footer } = Layout;

const MainPage = ({ children, title, ...rest }) => (
    <Layout>
        <SidebarLeft {...rest} />
        <Layout>
            <Header className="header">
                <p>{title}</p>
            </Header>
            <Content className="mainContent">
                {children}
            </Content>
            <Footer className="footerTk">Time Keeper v0.1 ©2020 Created by Lunatech</Footer>
        </Layout>
    </Layout>
);

export default MainPage