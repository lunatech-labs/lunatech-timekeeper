import React from 'react';

import MainPage from '../MainPage/MainPage';
import EditProjectForm from '../../components/Projects/EditProjectForm';
import {BrowserRouter as Router, Redirect, Route, Switch} from "react-router-dom";
import LoginPage from "../Login";
import {PrivateRoute} from "../../routes/utils";
import HomePage from "../Home";
import UsersPage from "../User/allUsers";
import NewProjectPage from "./newProject";
import DetailProjectPage from "./detailProject";
import ProjectsPage from "./allProjects";
import NewClientPage from "../Client/newClient";
import EditClientPage from "../Client/editClient";
import ClientsPage from "../Client/allClients";
import TimeEntriesPage from "../TimeSheet/timeEntriesPage";
import NewEventTemplatePage from "../Event/NewEventTemplatePage";
import EventsPage from "../Event/eventsPage";

const EditProjectsPage = () => {
  return (
    <MainPage title="Edit project">
      <EditProjectForm />
    </MainPage>
  );
};

export default EditProjectsPage;