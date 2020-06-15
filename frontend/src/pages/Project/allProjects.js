import React from 'react';
import MainPage from '../MainPage/MainPage';
import ProjectList from '../../components/Projects/ProjectList';
import {Link} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';
import {useKeycloak} from "@react-keycloak/web";

const ProjectsPage = () => {
  const [keycloak] = useKeycloak();
  const isAdmin = keycloak.hasRealmRole("admin");
  const actions = isAdmin && <Link id="tk_Btn" className="tk_BtnPrimary" key='addLink' to={'/projects/new'}>Add a project</Link>;
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