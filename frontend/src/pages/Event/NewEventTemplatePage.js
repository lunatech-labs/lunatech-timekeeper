import React from 'react';

import MainPage from '../MainPage/MainPage';
import NewEventTemplateForm from '../../components/Events/NewEventTemplateForm';

const NewEventTemplatePage = () => {
  return (
    <MainPage title="Create new event">
      <NewEventTemplateForm/>
    </MainPage>
  );
};

export default NewEventTemplatePage;