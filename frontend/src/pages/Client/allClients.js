import React from 'react';
import ClientList from '../../components/Clients/ClientList';
import MainPage from '../MainPage/MainPage';
import {Link} from 'react-router-dom';
import {Button} from 'antd';
import PlusOutlined from '@ant-design/icons/es/icons/PlusOutlined';

const ClientsPage = () => {
  return (
    <MainPage
      title="List of clients"
      actions={
        <Link to="/clients/new">
          <Button type="primary" style={{ width: 150 }} icon={<PlusOutlined className="tk_Icon_Mini"/>}>Add client</Button>
        </Link>
      }
    >
      <ClientList/>
    </MainPage>
  );
};

export default ClientsPage;