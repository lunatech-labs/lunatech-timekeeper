import React from 'react';
import MainPage from '../MainPage/MainPage';
import EditEventTemplateForm from '../../components/Events/EditEventTemplateForm';


const EditEventTemplatePage = () => {
  return (
    <MainPage title="Edit an event">
      <EditEventTemplateForm />
    </MainPage>
  );
};

export default EditEventTemplatePage;