import React from 'react'
import {Breadcrumb, Button, Cascader, DatePicker, Divider, Form, Input, PageHeader, Select, TreeSelect} from "antd";
import ShowUser from "../../components/ShowUser/ShowUser";
import './index.less';

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


            <Button onClick={callApi} type="primary">
                Load user profile
            </Button>

            <Divider/>
            <ShowUser user={loadedUsers}/>

        </React.Fragment>
    )
};

export default FakeContent