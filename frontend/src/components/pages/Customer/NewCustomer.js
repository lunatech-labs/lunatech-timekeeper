import React from 'react'
import CustomerForm from "./CustomerForm";

const NewCustomer = ({ axiosInstance }) => {
    return(
        <CustomerForm axiosInstance={axiosInstance} isNew={true}/>
    )
};

export default NewCustomer;