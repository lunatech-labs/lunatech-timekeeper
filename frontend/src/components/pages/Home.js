import React, { useState, useCallback } from 'react'

import { useKeycloak } from '@react-keycloak/web'
import { useAxios } from '../utils/hooks'
import ShowUser from './ShowUser'

import { PageHeader } from 'antd';
import { Layout, Menu, Breadcrumb } from 'antd';
import {
    DesktopOutlined,
    PieChartOutlined,
    FileOutlined,
    TeamOutlined,
    UserOutlined,
} from '@ant-design/icons';

import {
    Form,
    Input,
    Button,
    Select,
    Cascader,
    DatePicker,
    TreeSelect,
    Divider,
} from 'antd';

const { SubMenu } = Menu;
const { Header, Content, Sider, Footer } = Layout;

import './Home.scss'

export default () => {
    // Hook de React 16.8 https://reactjs.org/docs/hooks-overview.html
    const [loadedUsers, setData] = useState({ users: [] })

    const [componentSize, setComponentSize] = useState('small');
    const onFormLayoutChange = ({ size }) => {
        setComponentSize(size);
    };

    const { keycloak } = useKeycloak()
    const axiosInstance = useAxios('http://localhost:8080') // see https://github.com/panz3r/jwt-checker-server for a quick implementation
    const callApi = useCallback(() => {
        const fetchData = async () => {
            // Endpoint cote Quarkus qui prend le jeton JWT et retourne l'utilisateur authentifié
            const result = await axiosInstance.get('/api/users/me')
            setData(result.data);
        };
        fetchData();
    }, [axiosInstance]);

    return (
        <Layout>
            <Sider width={200} className="site-layout-background" collapsed={false}>
                <div className="logo">TimeKeeper</div>
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                <Menu.Item key="1">
                    <PieChartOutlined />
                    <span>Option 1</span>
                </Menu.Item>
                <Menu.Item key="2">
                    <DesktopOutlined />
                    <span>Option 2</span>
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
            <Layout>
                <Header className="header">
                    <p>Welcome</p>
                </Header>

                <Content className="mainContent">
                        <Breadcrumb style={{ margin: '16px 0' }}>
                            <Breadcrumb.Item>User</Breadcrumb.Item>
                            <Breadcrumb.Item>Sam</Breadcrumb.Item>
                        </Breadcrumb>

                        <PageHeader
                            className="site-page-header"
                            title="Welcome to TimeKeeper"
                            subTitle="A smart time entry application by Lunatech"
                        />

                        <div className="userPanel">User is {!keycloak.authenticated ? 'NOT ' : ''} authenticated</div>

            {!!keycloak.authenticated && (
                <Button onClick={() => keycloak.logout()} className="btn logout">
                    Logout
                </Button>
            )}

            <Button onClick={callApi} className="btn load">
                Load user profile
            </Button>

            <Divider></Divider>

            <ShowUser user={loadedUsers}/>
                        <Divider></Divider>

                        <Form
                            labelCol={{
                                span: 4,
                            }}
                            wrapperCol={{
                                span: 14,
                            }}
                            layout="horizontal"
                            initialValues={{
                                size: componentSize,
                            }}
                            onValuesChange={onFormLayoutChange}
                            size={componentSize}
                        >
                            <Form.Item label="Input">
                                <Input placeholder="Enter your TimeKeeper entry"/>
                            </Form.Item>
                            <Form.Item label="Select">
                                <Select>
                                    <Select.Option value="demo">DARVA - Agira</Select.Option>
                                    <Select.Option value="demo">DARVA - Sinapps</Select.Option>
                                    <Select.Option value="demo">Disney - Guest profile</Select.Option>
                                </Select>
                            </Form.Item>
                            <Form.Item label="TreeSelect">
                                <TreeSelect
                                    treeData={[
                                        {
                                            title: 'Darva',
                                            value: 'darva',
                                            children: [
                                                {
                                                    title: 'Agira',
                                                    value: 'Sinapps',
                                                },
                                            ],
                                        },
                                    ]}
                                />
                            </Form.Item>
                            <Form.Item label="Cascader">
                                <Cascader
                                    options={[
                                        {
                                            value: 'rc',
                                            label: 'RiskControl',
                                            children: [
                                                {
                                                    value: 'project1',
                                                    label: 'Impact',
                                                }, {
                                                    value: 'project2',
                                                    label: 'SPS',
                                                }
                                            ],
                                        },
                                    ]}
                                />
                            </Form.Item>
                            <Form.Item label="DatePicker">
                                <DatePicker />
                            </Form.Item>
                            <Form.Item label="Button">
                                <Button type="primary">Save</Button>
                                <Button type="danger">Cancel</Button>
                            </Form.Item>
                        </Form>

                    </Content>

                <Footer className="footerTk">Time Keeper v0.1 ©2020 Created by Lunatech</Footer>

            </Layout>
        </Layout>
    )
}
