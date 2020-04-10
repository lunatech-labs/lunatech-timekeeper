import React from 'react'
import {Layout, Menu} from "antd";
import {
    DesktopOutlined,
    PieChartOutlined,
    FileOutlined,
    TeamOutlined,
    UserOutlined,
} from '@ant-design/icons';

import logo from '../../img/logo.png'
import './Container.scss'
import LinkButton from "../atoms/LinkButton";


const { SubMenu } = Menu;
const { Sider } = Layout;

const ContainerSider = ({ }) => {

    return (
        <Sider width={200} className="site-layout-background" collapsed={false}>
            <img className="sider-logo" src={logo} alt="logo"/>
            <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                <Menu.Item key="1">
                    <PieChartOutlined />
                        <LinkButton
                            to="/home"
                            className="sider-link"
                            type="link"
                            htmlType="button"
                            ghost
                        >
                            Home
                        </LinkButton>
                </Menu.Item>
                <Menu.Item key="2">
                    <DesktopOutlined />
                        <LinkButton
                            to="/customers"
                            className="sider-link"
                            type="link"
                            ghost
                        >
                            Customers
                        </LinkButton>
                </Menu.Item>
                <SubMenu
                    key="sub1"
                    title={
                        <span>
                            <UserOutlined />
                            <span>User</span>
                        </span>
                    }
                >
                    <Menu.Item key="3">Tom</Menu.Item>
                    <Menu.Item key="4">Bill</Menu.Item>
                    <Menu.Item key="5">Alex</Menu.Item>
                </SubMenu>
                <SubMenu
                    key="sub2"
                    title={
                        <span>
                            <TeamOutlined />
                            <span>Team</span>
                        </span>
                    }
                >
                    <Menu.Item key="6">Team 1</Menu.Item>
                    <Menu.Item key="8">Team 2</Menu.Item>
                </SubMenu>
                <Menu.Item key="9">
                    <FileOutlined />
                </Menu.Item>
            </Menu>
        </Sider>
    )
};

export default ContainerSider