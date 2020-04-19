import React from 'react';

import NewClientForm from '../../components/Clients/NewClientForm.js';
import MainPage from '../MainPage/MainPage';

const NewClientPage = () => {
  return (
    <MainPage title="Add new client">
      <NewClientForm/>
    </MainPage>
  );
};

export default NewClientPage;