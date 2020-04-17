import React from 'react'
import { BrowserRouter as Router, Redirect, Switch, Route } from 'react-router-dom'

import { useKeycloak } from '@react-keycloak/web'

import HomePage from '../pages/Home/index'
import LoginPage from '../pages/Login/index'
import ClientsPage from '../pages/Client/index'

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
                <PrivateRoute path="/clients" component={ClientsPage} />
                <Redirect from="/" to="/home" />
            </Switch>
        </Router>
    )
}
