import React from 'react';
import { BrowserRouter as Router, Redirect, Switch, Route } from 'react-router-dom';

import { useKeycloak } from '@react-keycloak/web';

import HomePage from '../pages/Home/index';
import LoginPage from '../pages/Login/index';
import ClientsPage from '../pages/Client/allClients.js';
import NewClientPage from '../pages/Client/newClient.js';
import EditClientPage from '../pages/Client/editClient.js';

import { PrivateRoute } from './utils';
import UsersPage from '../pages/User/allUsers';
import ProjectsPage from '../pages/Project/allProjects';
import NewProjectPage from '../pages/Project/newProject';
import DetailProjectPage from '../pages/Project/detailProject';

export const AppRouter = () => {
  const [, initialized] = useKeycloak();

  if (!initialized) {
    return <div>Loading...</div>;
  }

  return (
    <Router>
      <Switch>
        <Route        path="/login"        component={LoginPage} />
        <PrivateRoute exact path="/home"   component={HomePage} />
        <PrivateRoute path="/users"        component={UsersPage} roles={['admin']} />
        <PrivateRoute path="/projects/new" component={NewProjectPage} roles={['admin']} />
        <PrivateRoute path="/projects/:id" component={DetailProjectPage} />
        <PrivateRoute path="/projects"     component={ProjectsPage} />
        <PrivateRoute path="/clients/new"  component={NewClientPage} roles={['admin']} />
        <PrivateRoute path="/clients/:id"  component={EditClientPage} roles={['admin']} />
        <PrivateRoute path="/clients"      component={ClientsPage} roles={['admin']} />
        {/* the /clients route must be after any other clients routes, else it does not work*/}
        <Redirect from="/" to="/home" />
      </Switch>
    </Router>
  );
};
