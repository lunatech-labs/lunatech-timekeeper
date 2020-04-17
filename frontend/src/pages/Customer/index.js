import React, {useEffect, useState} from 'react'
import { useLocation } from "react-router-dom";
import CustomerList from './CustomerList';
import MainPage from "../../MainPage/MainPage";
import CustomerForm from "./CustomerForm";
import logo from '../../img/logo_timekeeper_homepage.png';
import MainPage from "../MainPage/MainPage";
import {useAxios} from "../../utils/hooks";

const getCustomerList = (axios, setState) => {
    const fetchData = async () => {
        const result = await axios.get('/api/customers');
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

const selectCustomer = (pathname, customers) => {
    const splitPathname = pathname.split('/');
    if(splitPathname && splitPathname.length > 2) {
        if(splitPathname[2] === 'new') {
            return 'new';
        } else {
            return customers.find(c => c.id.toString() === splitPathname[2]);
        }
    } else {
        return null;
    }
};

const CustomersPage = () => {
    const [customers, setCustomers] = useState([]);
    const [activities, setActivities] = useState([]);
    const apiEndpoint = useAxios('http://localhost:8080');
    const { pathname } = useLocation();
    const selectedCustomer = selectCustomer(pathname, customers);

    useEffect(() => {
        if(!apiEndpoint) {
            return;
        }

        if(!selectedCustomer) {
            getCustomerList(apiEndpoint, setCustomers);
            getActivityList(apiEndpoint, setActivities);
        }
    }, [apiEndpoint, selectedCustomer]);

    return (
        selectedCustomer
            ? selectedCustomer === 'new'
                ? (
                    <MainPage title="Add new customer">
                        <CustomerForm axiosInstance={apiEndpoint} isNew={true} />
                    </MainPage>
                )
                : (
                    <MainPage title={`Edit ${selectedCustomer.name}`}>
                        <CustomerForm customer={selectedCustomer} axiosInstance={apiEndpoint} isNew={false} />
                    </MainPage>
                )
            : (
                <MainPage title="All customers">
                    <CustomerList customers={customers} logo={logo} activities={activities}/>
                </MainPage>
            )
    )
};

export default CustomersPage;