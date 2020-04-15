import React, {useEffect, useState} from 'react'
import { useLocation } from "react-router-dom";
import CustomerList from './CustomerList';
import logo from '../../../logo_timekeeper_homepage.png';
import CustomerDetails from "./CustomerDetails";
import MainContainer from "../../container/MainContainer";
import NewCustomer from "./NewCustomer";
import {useAxios} from "../../../utils/hooks";

const list = [{"id": 1, "name": "Paul"}, {"id": 2, "name": "Nicolas"},
    {"id":3, "name": "Marie"}, {"id": 4, "name": "Stephan"}, {"id": 5, "name": "Camille"}];


const CustomersPage = ({ }) => {
    const [customers, setCustomers] = useState([]);
    const [activities, setActivities] = useState([]);
    const apiEndpoint = useAxios('http://localhost:8080');
    const { pathname } = useLocation();
    const selectedCustomer = pathname.split('/').length > 2
        ? pathname.split('/')[2] === 'new'
            ? 'new'
            : list.find(c => c.id = pathname.split('/')[2])
        : null;
    const getCustomerList = () => {
        const fetchData = async () => {
            const result = await apiEndpoint.get('/api/customers');
            setCustomers(result.data);
        };
        fetchData();
    };
    const getActivities = () => {
        const fetchData = async () => {
            const result = await apiEndpoint.get('/api/activities');
            setActivities(result.data);
        };
        fetchData();
    };
    useEffect(() => {
        if(!apiEndpoint) {
            return;
        }

        if(!selectedCustomer) {
            getCustomerList();
            getActivities();
        }
        return () => {
            setCustomers([]);
            setActivities([]);
        }
    }, [apiEndpoint, selectedCustomer]);

    return (
        selectedCustomer
            ? selectedCustomer === 'new'
                ? (
                    <MainContainer title="Add new customer">
                        <NewCustomer list={list} logo={logo}/>
                    </MainContainer>
                )
                : (
                    <MainContainer title={`About ${selectedCustomer.name}`}>
                        <CustomerDetails customer={selectedCustomer}/>
                    </MainContainer>
                )
            : (
                <MainContainer title="All customers">
                    <CustomerList list={customers} logo={logo} activities={activities}/>
                </MainContainer>
            )
    )
};

export default CustomersPage;