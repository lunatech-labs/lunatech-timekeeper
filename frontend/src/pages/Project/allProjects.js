import React from 'react';
import MainPage from '../MainPage/MainPage';
import ProjectList from '../../components/Projects/ProjectList';
import {Link} from 'react-router-dom';
import '../../components/Button/BtnGeneral.less';

const ProjectsPage = () => {
  return (
    <MainPage
      title={'List of projects'}
      actions={<Link id="tk_Btn" className="tk_BtnPrimary" key='addLink' to={'/projects/new'}>Add a project</Link>}
    >
      <ProjectList/>
    </MainPage>
  );
};
export default ProjectsPage;