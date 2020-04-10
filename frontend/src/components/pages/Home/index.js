import React, { useState, useCallback } from 'react'
import { useKeycloak } from '@react-keycloak/web'
import { useAxios } from '../../../utils/hooks'
import FakeContent from "./FakeContent";
import MainContainer from "../../container/MainContainer";

const fake = true;

export default () => {
    // Hook de React 16.8 https://reactjs.org/docs/hooks-overview.html
    const [loadedUsers, setData] = useState({ users: [] });

    const [componentSize, setComponentSize] = useState('small');
    const onFormLayoutChange = ({ size }) => {
        setComponentSize(size);
    };

    const { keycloak } = useKeycloak();
    const axiosInstance = useAxios('http://localhost:8080');
    // see https://github.com/panz3r/jwt-checker-server for a quick implementation
    const callApi = useCallback(() => {
        const fetchData = async () => {
            // Endpoint cote Quarkus qui prend le jeton JWT et retourne l'utilisateur authentifié
            const result = await axiosInstance.get('/api/users/me');
            setData(result.data);
        };
        fetchData();
    }, [axiosInstance]);

    return (
        <MainContainer title="Welcome">
            {fake && (
                <FakeContent
                    keycloak={keycloak}
                    callApi={callApi}
                    componentSize={componentSize}
                    onFormLayoutChange={onFormLayoutChange}
                    loadedUsers={loadedUsers}
                />
            )}
        </MainContainer>
    )
}
