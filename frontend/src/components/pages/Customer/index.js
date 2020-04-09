import React, { useState, useCallback } from 'react'
import CustomerList from './CustomerList';
import logo from '../../../logo_timekeeper_homepage.png';
import CustomerDetails from "./CustomerDetails";

const list = [{"id": 1, "name": "Paul"}, {"id": 2, "name": "Nicolas"}, 
    {"id":3, "name": "Marie"}, {"id": 4, "name": "Stephan"}, {"id": 5, "name": "Camille"}];

const CustomersPage = ({ }) => {
    const [customer, setCustomer] = useState(null);
    return (
        customer
            ? (<CustomerDetails />)
            : (<CustomerList list={list} logo={logo}/>)

    ) 
};

export default CustomersPage;