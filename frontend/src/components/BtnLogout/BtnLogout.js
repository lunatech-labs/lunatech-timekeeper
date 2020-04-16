import React, { Component } from 'react';
import {Button, Menu} from "antd";
import './BtnLogout.scss';
import {useKeycloak} from "@react-keycloak/web";

const BtnLogout = ({ }) => {
    const { keycloak } = useKeycloak();

    return (
        <React.Fragment>
            {!!keycloak.authenticated && (
                <Button onClick={() => keycloak.logout()} className="btn logout">
                    Logout
                </Button>
            )}
        </React.Fragment>
    )
}

export default BtnLogout;