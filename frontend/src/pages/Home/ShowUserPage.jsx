import React from 'react';
import {Alert} from 'antd';
import ShowUser from 'components/Users/ShowUser';
import './index.less';
import {useTimeKeeperAPI} from 'utils/services';

const ShowUserPage = () => {

    // Do not rename variables below - Keep 'data', 'error' or else it will not work
    const { data, error, loading } = useTimeKeeperAPI('/api/users/me');

    if (error) {
        return (
            <React.Fragment>
                <Alert title='Server error'
                       message='Failed to load user from Quarkus backend server'
                       type='error'
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
            <ShowUser user={data}/>
        </React.Fragment>
    )
};

export default ShowUserPage