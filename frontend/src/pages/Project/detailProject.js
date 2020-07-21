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

import React, {useCallback} from 'react';
import MainPage from '../MainPage/MainPage';
import ShowProject from '../../components/Projects/ShowProject';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert} from 'antd';
import {Link, useRouteMatch} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';
import {canEditProject} from '../../utils/rights';
import {useKeycloak} from '@react-keycloak/web';

const DetailProjectPage = () => {


  const projectIdSlug = useRouteMatch({
    path: '/projects/:id',
    strict: true,
    sensitive: true
  });

  const [keycloak] = useKeycloak();

  const projectId = projectIdSlug.params.id;

  const useTimeKeeperAPICurrentUser = useTimeKeeperAPI('/api/users/me');
  const useTimeKeeperAPIProject = useTimeKeeperAPI('/api/projects/' + projectId);
  const {run} = useTimeKeeperAPICurrentUser;

  const onSuccessCallback = useCallback(() => {
    run();
  }, [run]);

  if (useTimeKeeperAPIProject.error || useTimeKeeperAPICurrentUser.error) {
    let errorReason = 'Message: ' + useTimeKeeperAPIProject.error;
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load projects or current user from Quarkus backend server'
          type='error'
          description={errorReason}
        />
      </React.Fragment>
    );
  }
  if (useTimeKeeperAPIProject.loading || useTimeKeeperAPICurrentUser.loading) {
    return (
      <div>loading...</div>
    );
  }

  const actions = canEditProject(useTimeKeeperAPIProject.data, useTimeKeeperAPICurrentUser.data, keycloak)
    && <Link id="btnEditProject" className="tk_Btn tk_BtnPrimary" to={`/projects/${useTimeKeeperAPIProject.data.id}/edit`}>Edit project</Link>;
  return (
    <MainPage title="Project details" entityName={useTimeKeeperAPIProject.data.name}
      actions={
        actions
      }>
      <ShowProject project={useTimeKeeperAPIProject.data} onSuccessJoinProject={onSuccessCallback} />
    </MainPage>
  );
};
export default DetailProjectPage;