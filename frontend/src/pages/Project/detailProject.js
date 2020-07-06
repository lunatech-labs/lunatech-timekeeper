import React, {useCallback, useContext} from 'react';
import MainPage from '../MainPage/MainPage';
import ShowProject from '../../components/Projects/ShowProject';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert} from 'antd';
import {Link, useRouteMatch} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';
import {canEditProject} from "../../utils/rights";
import {useKeycloak} from "@react-keycloak/web";
import {UserContext} from "../../context/UserContext";

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
    && <Link id="tk_Btn" className="tk_BtnPrimary" to={`/projects/${useTimeKeeperAPIProject.data.id}/edit`}>Edit project</Link>;
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