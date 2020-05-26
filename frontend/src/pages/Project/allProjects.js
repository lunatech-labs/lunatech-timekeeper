import React from 'react';
import MainPage from '../MainPage/MainPage';
import ProjectList from '../../components/Projects/ProjectList';
import {Link} from 'react-router-dom';
import PlusOutlined from '@ant-design/icons/lib/icons/PlusOutlined';

const ProjectsPage = () => {
  return (
    <MainPage
      title={'List of projects'}
      actions={<Link id="tk_Btn" className="tk_BtnPrimary" key='addLink' to={'/projects/new'}><PlusOutlined />Add a project</Link>}
    >
      <ProjectList/>
    </MainPage>
  );
};
export default ProjectsPage;