import React, {useEffect, useState} from 'react'
import { useLocation } from "react-router-dom";
import {useAxios} from "../../utils/hooks";
import ClientList from './ClientList';
import ClientForm from "./ClientForm";
import MainPage from "../MainPage/MainPage";
import logo from '../../img/logo_timekeeper_homepage.png';

const getClientList = (axios, setState) => {
    const fetchData = async () => {
        const result = await axios.get('/api/clients');
        setState(result.data);
    };
    fetchData();
};
const getActivityList = (axios, setState) => {
    const fetchData = async () => {
        const result = await axios.get('/api/activities');
        setState(result.data);
    };
    fetchData();
};

const selectClient = (pathname, clients) => {
    const splitPathname = pathname.split('/');
    if(splitPathname && splitPathname.length > 2) {
        if(splitPathname[2] === 'new') {
            return 'new';
        } else {
            return clients.find(c => c.id.toString() === splitPathname[2]);
        }
    } else {
        return null;
    }
};

const ClientsPage = () => {
    const [clients, setClients] = useState([]);
    const [activities, setActivities] = useState([]);
    const apiEndpoint = useAxios('http://localhost:8080');
    const { pathname } = useLocation();
    const selectedClient = selectClient(pathname, clients);

    useEffect(() => {
        if(!apiEndpoint) {
            return;
        }

        if(!selectedClient) {
            getClientList(apiEndpoint, setClients);
            getActivityList(apiEndpoint, setActivities);
        }
    }, [apiEndpoint, selectedClient]);

    return (
        selectedClient
            ? selectedClient === 'new'
                ? (
                    <MainPage title="Add new client">
                        <ClientForm axiosInstance={apiEndpoint} isNew={true} />
                    </MainPage>
                )
                : (
                    <MainPage title={`Edit ${selectedClient.name}`}>
                        <ClientForm client={selectedClient} axiosInstance={apiEndpoint} isNew={false} />
                    </MainPage>
                )
            : (
                <MainPage title="All clients">
                    <ClientList clients={clients} logo={logo} activities={activities}/>
                </MainPage>
            )
    )
};

export default ClientsPage;