import React from 'react';
import ClientList from '../../components/Clients/ClientList';
import MainPage from '../MainPage/MainPage';
import {Link} from 'react-router-dom';
import {Button, Space} from 'antd';
import PlusOutlined from '@ant-design/icons/lib/icons/PlusOutlined';
import {CheckOutlined, CloseOutlined} from "@ant-design/icons";

const ClientsPage = () => {
  return (
    <MainPage
      title="List of clients"
      actions={
        <Link id="tk_Btn" className="tk_BtnPrimary" to={'/clients/new'}><PlusOutlined />Add Client</Link>
      }
    >
      <ClientList/>
    </MainPage>
  );
};

export default ClientsPage;