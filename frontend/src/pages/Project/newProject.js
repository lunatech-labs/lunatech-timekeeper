import React from 'react';

import MainPage from 'pages/MainPage/MainPage';
import NewProjectForm from 'components/Projects/NewProjectForm';

const NewProjectPage = () => {
  return (
    <MainPage title="Create new project">
      <NewProjectForm/>
    </MainPage>
  );
};

export default NewProjectPage;