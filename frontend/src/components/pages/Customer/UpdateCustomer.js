import React from 'react'
import PropTypes from 'prop-types'
import CustomerForm from "./CustomerForm";

const UpdateCustomer = ({ customer, axiosInstance }) => {

    return(
        <CustomerForm customer={customer} axiosInstance={axiosInstance} isNew={false}/>
    );
};

UpdateCustomer.propTypes = {
    customer: PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string,
        description: PropTypes.string
    })
};

export default UpdateCustomer;