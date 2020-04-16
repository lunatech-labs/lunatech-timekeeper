import React, { Component } from 'react';
import {Link} from "react-router-dom";
import {Menu} from "antd";
import './MenuSidebar.scss';

import {DesktopOutlined, PieChartOutlined, TeamOutlined, UserOutlined} from "@ant-design/icons";
import LinkButton from "../atoms/LinkButton";

const { SubMenu } = Menu;

class MenuSidebar extends Component {
    render() {
        return (
            <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline" className="tk_Menu">
                <Menu.Item key="1">
                    <PieChartOutlined />
                    <Link
                        to="/home"
                        className="sider-link"
                        type="link"
                        ghost
                    >
                        Home
                    </Link>
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
            </Menu>
        )
    }
}

export default MenuSidebar;