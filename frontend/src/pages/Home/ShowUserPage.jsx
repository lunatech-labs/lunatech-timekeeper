import React from 'react'
import {Breadcrumb, Divider, PageHeader, Alert} from "antd";
import ShowUser from "../../components/Users/ShowUser";
import './index.less';
import { useRequest } from '@umijs/hooks';
import '../../utils/request';

const ShowUserPage = () => {

    // Do not rename variables below - Keep 'data', 'error' or else it will not work
    const { data, error, loading } = useRequest('http://localhost:8080/api/users/me');

    if (error) {
        let errorReason = "Message: " + error ;
        return (
            <React.Fragment>
                <Alert title='Server error'
                       message='Failed to load user from Quarkus backend server'
                       type='error'
                       description={errorReason}
                />
            </React.Fragment>
        );
    }
    if (loading) {
        return (
            <div>loading...</div>
        );
    }

    return (

        <React.Fragment>
            <Breadcrumb style={{margin: '16px 0'}}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>Welcome</Breadcrumb.Item>
            </Breadcrumb>

            <PageHeader
                className="site-page-header"
                title="Welcome to TimeKeeper"
                subTitle="A smart time entry application by Lunatech"
            />

            <Divider/>
            <ShowUser user={data}/>

        </React.Fragment>
    )
};

export default ShowUserPage