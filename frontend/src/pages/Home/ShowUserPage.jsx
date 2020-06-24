/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import {Alert} from 'antd';
import ShowUser from '../../components/Users/ShowUser';
import './index.less';
import {useTimeKeeperAPI} from '../../utils/services';

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