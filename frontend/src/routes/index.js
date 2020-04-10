import React from 'react'
import { BrowserRouter as Router, Redirect, Switch, Route } from 'react-router-dom'

import { useKeycloak } from '@react-keycloak/web'

import HomePage from '../components/pages/Home/index'
import LoginPage from '../components/pages/Login/index'
import CustomersPage from '../components/pages/Customer/index'

import { PrivateRoute } from './utils'

export const AppRouter = () => {
    const [, initialized] = useKeycloak();

    if (!initialized) {
        return <div>Loading...</div>
    }

    return (
        <Router>
            <Switch>
                <PrivateRoute exact path="/home" component={HomePage} />
                <Route path="/login" component={LoginPage} />
                <Route path="/customers" component={CustomersPage} />
                <Redirect from="/" to="/home" />
            </Switch>
        </Router>
    )
}
