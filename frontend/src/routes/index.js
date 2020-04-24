import React from 'react';
import { BrowserRouter as Router, Redirect, Switch, Route } from 'react-router-dom';

import { useKeycloak } from '@react-keycloak/web';

import HomePage from '../pages/Home/index';
import LoginPage from '../pages/Login/index';
import ClientsPage from '../pages/Client/allClients.js';
import NewClientPage from '../pages/Client/newClient.js';
import EditClientPage from '../pages/Client/editClient.js';

import { PrivateRoute } from './utils';
import UsersPage from "../pages/User/allUsers";

export const AppRouter = () => {
  const [, initialized] = useKeycloak();

  if (!initialized) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <Switch>
        <Route path="/login"              component={LoginPage} />
        <PrivateRoute exact path="/home"  component={HomePage} />
        {/*Users*/}
        <PrivateRoute path="/users"       component={UsersPage} roles={["user"]} />
        {/*Clients*/}
        <PrivateRoute path="/clients/new" component={NewClientPage} roles={["admin"]} />
        <PrivateRoute path="/clients/:id" component={EditClientPage} roles={["admin"]} />
        <PrivateRoute path="/clients"     component={ClientsPage} roles={["admin"]} />
        {/* the /clients route must be after any other clients routes, else it does not work*/}
        <Redirect from="/" to="/home" />
      </Switch>
    </Router>
  );
};
