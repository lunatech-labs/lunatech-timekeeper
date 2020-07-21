/*
 * Copyright 2020 Lunatech S.A.S
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
import MainPage from '../MainPage/MainPage';
import ProjectList from '../../components/Projects/ProjectList';
import {Link} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';
import {useKeycloak} from '@react-keycloak/web';

const ProjectsPage = () => {
  const [keycloak] = useKeycloak();
  const isAdmin = keycloak.hasRealmRole('admin');
  const actions = isAdmin && <Link id="btnAddProject" className="tk_Btn tk_BtnPrimary" key='addLink' to={'/projects/new'}>Add a project</Link>;
  return (
    <MainPage
      title={'List of projects'}
      actions={actions}
    >
      <ProjectList/>
    </MainPage>
  );
};
export default ProjectsPage;