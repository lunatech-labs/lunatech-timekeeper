import React from 'react'
import { useLocation } from "react-router-dom";
import CustomerList from './CustomerList';
import logo from '../../../logo_timekeeper_homepage.png';
import CustomerDetails from "./CustomerDetails";
import MainPage from "../../MainPage/MainPage";
import NewCustomer from "./NewCustomer";

const list = [{"id": 1, "name": "Paul"}, {"id": 2, "name": "Nicolas"},
    {"id":3, "name": "Marie"}, {"id": 4, "name": "Stephan"}, {"id": 5, "name": "Camille"}];

const CustomersPage = ({ }) => {
    const { pathname } = useLocation();
    const selectedCustomer = pathname.split('/').length > 2
        ? pathname.split('/')[2] === 'new'
            ? 'new'
            : list.find(c => c.id = pathname.split('/')[2])
        : null;

    return (
        selectedCustomer
            ? selectedCustomer === 'new'
                ? (
                    <MainPage title="Add new customer">
                        <NewCustomer list={list} logo={logo}/>
                    </MainPage>
                )
                : (
                    <MainPage title={`About ${selectedCustomer.name}`}>
                        <CustomerDetails customer={selectedCustomer}/>
                    </MainPage>
                )
            : (
                <MainPage title="All customers">
                    <CustomerList list={list} logo={logo}/>
                </MainPage>
            )
    )
};

export default CustomersPage;