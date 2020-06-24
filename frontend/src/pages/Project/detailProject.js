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
import {Alert, Button} from 'antd';
import {Link, useRouteMatch} from 'react-router-dom';
import EditOutlined from '@ant-design/icons/lib/icons/EditOutlined';
import '../../components/Button/BtnGeneral.less';

const DetailProjectPage = () => {

  const projectIdSlug = useRouteMatch({
    path: '/projects/:id',
    strict: true,
    sensitive: true
  });

  const projectId = projectIdSlug.params.id;

  const {data, error, loading, run} = useTimeKeeperAPI('/api/projects/' + projectId);

  const onSuccessCallback = useCallback(() => {
    run();
  }, [run]);

  if (error) {
    let errorReason = 'Message: ' + error;
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load projects from Quarkus backend server'
          type='error'
          description={errorReason}
        />
      </React.Fragment>
    );
  }
  if (loading) {
    return (
      <div>loading...</div>
    );
  }

  return (
    <MainPage title="Project details" entityName={data.name}
      actions={<Link key='editLink' to={`/projects/${data.id}/edit`}>
        <Button id="tk_Btn"
          className="tk_BtnPrimary"
          icon={<EditOutlined/>}>Edit project
        </Button>
      </Link>}>
      <ShowProject project={data} onSuccessJoinProject={onSuccessCallback} />
    </MainPage>
  );
};
export default DetailProjectPage;