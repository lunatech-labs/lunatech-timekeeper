import React from 'react'
import {Breadcrumb, Button, Cascader, DatePicker, Divider, Form, Input, PageHeader, Select, TreeSelect} from "antd";
import ShowUser from "../../ShowUser";

const FakeContent = ({ keycloak, callApi, loadedUsers, componentSize, onFormLayoutChange }) => {

    return (
        <React.Fragment>
            <Breadcrumb style={{margin: '16px 0'}}>
                <Breadcrumb.Item>User</Breadcrumb.Item>
                <Breadcrumb.Item>Sam</Breadcrumb.Item>
            </Breadcrumb>

            <PageHeader
                className="site-page-header"
                title="Welcome to TimeKeeper"
                subTitle="A smart time entry application by Lunatech"
            />

            <div className="userPanel">User is {!keycloak.authenticated ? 'NOT ' : ''} authenticated</div>


            <Button onClick={callApi} className="btn load">
                Load user profile
            </Button>

            <Divider/>
            <ShowUser user={loadedUsers}/>
            <Divider/>

            <Form
                labelCol={{span: 4}}
                wrapperCol={{span: 14}}
                layout="horizontal"
                initialValues={{size: componentSize}}
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
                    <DatePicker/>
                </Form.Item>
                <Form.Item label="Button">
                    <Button type="primary">Save</Button>
                    <Button type="danger">Cancel</Button>
                </Form.Item>
            </Form>
        </React.Fragment>
    )
};

export default FakeContent