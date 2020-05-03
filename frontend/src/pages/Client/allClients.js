import React from 'react';
import ClientList from '../../components/Clients/ClientList';
import MainPage from '../MainPage/MainPage';

const ClientsPage = () => {
  return (
    <MainPage title="List of clients">
      <ClientList/>
    </MainPage>
  );
};

export default ClientsPage;