import React, {useEffect, useState} from 'react'
import { useLocation } from "react-router-dom";
import CustomerList from './CustomerList';
import logo from '../../../logo_timekeeper_homepage.png';
import CustomerDetails from "./CustomerDetails";
import MainContainer from "../../container/MainContainer";
import NewCustomer from "./NewCustomer";
import {useAxios} from "../../../utils/hooks";

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
    if(splitPathname.length > 2) {
        if(splitPathname[2] === 'new') {
            return 'new';
        } else {
            return customers.find(c => c.id = splitPathname[2]);
        }
    } else {
        return null;
    }
};

const CustomersPage = ({ }) => {
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
            getCustomerList(apiEndpoint,customers => setCustomers(customers));
            getActivityList(apiEndpoint,activities => setActivities(activities));
        }
    }, [apiEndpoint, selectedCustomer]);

    return (
        selectedCustomer
            ? selectedCustomer === 'new'
                ? (
                    <MainContainer title="Add new customer">
                        <NewCustomer list={customers} logo={logo}/>
                    </MainContainer>
                )
                : (
                    <MainContainer title={`About ${selectedCustomer.name}`}>
                        <CustomerDetails customer={selectedCustomer}/>
                    </MainContainer>
                )
            : (
                <MainContainer title="All customers">
                    <CustomerList customers={customers} logo={logo} activities={activities}/>
                </MainContainer>
            )
    )
};

export default CustomersPage;