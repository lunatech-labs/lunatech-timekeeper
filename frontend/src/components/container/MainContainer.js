import React from 'react'
import { Layout } from "antd";
import './Container.scss'
import ContainerSider from "./ContainerSider";

const { Header, Content, Footer } = Layout;

const MainContainer = ({ children, title, ...rest }) => (
    <Layout>
        <ContainerSider {...rest} />
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

export default MainContainer