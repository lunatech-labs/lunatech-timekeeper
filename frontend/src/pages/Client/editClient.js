import React from 'react';
import EditClientForm from 'components/Clients/EditClientForm';
import MainPage from 'pages/MainPage/MainPage';


const EditClientPage = () => {
  return (
    <MainPage title="Edit a client">
      <EditClientForm/>
    </MainPage>
  );
};

export default EditClientPage;