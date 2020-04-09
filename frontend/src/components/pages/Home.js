import React, { useState, useCallback } from 'react'

import { useKeycloak } from '@react-keycloak/web'
import { useAxios } from '../../utils/hooks'
import ShowUser from '../ShowUser'
import { Button } from 'antd'

import 'antd/dist/antd.css';
import './Home.scss'

export default () => {
    // Hook de React 16.8 https://reactjs.org/docs/hooks-overview.html
    const [loadedUsers, setData] = useState({ users: [] })
    const { keycloak } = useKeycloak();

    const axiosInstance = useAxios('http://localhost:8080'); // see https://github.com/panz3r/jwt-checker-server for a quick implementation
    const callApi = useCallback(() => {
        const fetchData = async () => {
            // Endpoint cote Quarkus qui prend le jeton JWT et retourne l'utilisateur authentifié
            const result = await axiosInstance.get('/api/users/me')
            setData(result.data);
        };
        fetchData();
    }, [axiosInstance]);

    return (
        <div>
            <div className="userPanel">User is {!keycloak.authenticated ? 'NOT ' : ''} authenticated</div>

            {!!keycloak.authenticated && (
                <Button onClick={() => keycloak.logout()} className="btn logout">
                    Logout
                </Button>
            )}

            <Button onClick={callApi} className="btn load">
                Load user profile
            </Button>

            <ShowUser user={loadedUsers}/>

        </div>
    )
}
