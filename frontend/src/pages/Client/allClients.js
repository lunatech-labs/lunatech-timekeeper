/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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