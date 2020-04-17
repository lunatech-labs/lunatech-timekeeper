import React, {useEffect, useState} from 'react'
import {BrowserRouter as Router, Redirect, Route, Switch} from "react-router-dom";
import { useLocation } from "react-router-dom";
import CustomerList from './CustomerList';
import CustomerForm from "./CustomerForm";
import logo from '../../img/logo_timekeeper_homepage.png';
import MainPage from "../MainPage/MainPage";
import {useAxios} from "../../utils/hooks";
import {PrivateRoute} from "../../routes/utils";

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

const CustomersPage = ({ match }) => {
    const [customers, setCustomers] = useState([]);
    const [activities, setActivities] = useState([]);
    const apiEndpoint = useAxios('http://localhost:8080');
    const { pathname } = useLocation();

    useEffect(() => {
        if(!apiEndpoint) {
            return;
        }

        getCustomerList(apiEndpoint, setCustomers);
        getActivityList(apiEndpoint, setActivities);

    }, [apiEndpoint]);


    return (
        <Router>
            <Switch>
                <PrivateRoute exact path={`${match.url}`}>
                    <MainPage title="All customers">
                        <CustomerList customers={customers} logo={logo} activities={activities}/>
                    </MainPage>
                </PrivateRoute>
                <PrivateRoute exact path={`${match.url}/new`}>
                    <MainPage title="Add new customer">
                        <CustomerForm axiosInstance={apiEndpoint} isNew={true}/>
                    </MainPage>
                </PrivateRoute>
                <PrivateRoute exact path={`${match.url}/:id`}>
                    <MainPage title={`Edit`}>
                        <CustomerForm axiosInstance={apiEndpoint} isNew={false} />
                    </MainPage>
                </PrivateRoute>
            </Switch>
        </Router>
    )
};

export default CustomersPage;