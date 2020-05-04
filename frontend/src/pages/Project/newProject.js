import React from 'react';

import MainPage from '../MainPage/MainPage';
import NewProjectForm from '../../components/Projects/NewProjectForm';

const NewProjectPage = () => {
  return (
    <MainPage title="Add new project">
      <NewProjectForm/>
    </MainPage>
  );
};

export default NewProjectPage;