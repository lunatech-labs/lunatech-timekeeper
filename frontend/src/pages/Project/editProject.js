import React from 'react';

import MainPage from 'pages/MainPage/MainPage';
import EditProjectForm from 'components/Projects/EditProjectForm';

const EditProjectsPage = () => {
  return (
    <MainPage title="Edit project">
      <EditProjectForm />
    </MainPage>
  );
};

export default EditProjectsPage;