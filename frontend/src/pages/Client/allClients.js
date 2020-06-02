import React from 'react';
import ClientList from '../../components/Clients/ClientList';
import MainPage from '../MainPage/MainPage';
import {Link} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';

const ClientsPage = () => {
  return (
    <MainPage
      title="List of clients"
      actions={
        <Link id="tk_Btn" className="tk_BtnPrimary" to={'/clients/new'}>Add Client</Link>
      }
    >
      <ClientList/>
    </MainPage>
  );
};

export default ClientsPage;