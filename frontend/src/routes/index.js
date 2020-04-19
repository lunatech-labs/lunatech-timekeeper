import React from 'react'
import { BrowserRouter as Router, Redirect, Switch, Route } from 'react-router-dom'

import { useKeycloak } from '@react-keycloak/web'

import HomePage from '../pages/Home/index'
import LoginPage from '../pages/Login/index'
import ClientsPage from '../pages/Client/allClients.js'
import NewClientPage from '../pages/Client/newClient.js'
import EditClientPage from '../pages/Client/editClient.js'

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
                <PrivateRoute path="/clients/new" component={NewClientPage} />
                <PrivateRoute path="/clients/:id" component={EditClientPage} />
                <PrivateRoute path="/clients"     component={ClientsPage} />
                {/* the /clients route must be after any other clients routes, else it does not work*/}
                <Redirect from="/" to="/home" />
            </Switch>
        </Router>
    )
}
