import React, {useCallback} from 'react';
import MainPage from '../MainPage/MainPage';
import ShowProject from '../../components/Projects/ShowProject';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert} from 'antd';
import {Link, useRouteMatch} from 'react-router-dom';
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
      actions={
        <Link id="tk_Btn" className="tk_BtnPrimary" to={`/projects/${data.id}/edit`}>Edit project</Link>
      }>
      <ShowProject project={data} onSuccessJoinProject={onSuccessCallback} />
    </MainPage>
  );
};
export default DetailProjectPage;