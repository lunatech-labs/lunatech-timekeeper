import React, { Component } from 'react';
import {
    MenuUnfoldOutlined,
    MenuFoldOutlined,
} from '@ant-design/icons';
import {Layout} from "antd";

const { Header } = Layout;

const TopBar = ({ collapsed, toggle }) => {
    return (
        <Header className="site-layout-background" style={{ padding: 0 }}>
            {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
                className: 'trigger',
                onClick: toggle,
            })}
        </Header>
    )
};

export default TopBar