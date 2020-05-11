import React from 'react';
import MainPage from '../MainPage/MainPage';
import ProjectList from '../../components/Projects/ProjectList';
import {Link} from 'react-router-dom';
import {Button} from 'antd';
import PlusOutlined from '@ant-design/icons/lib/icons/PlusOutlined';

const ProjectsPage = () => {
  return (
    <MainPage
      title={'List of projects'}
      actions={<Link key='addLink' to='/projects/new'><Button type="primary" icon={<PlusOutlined/>}>Add a project</Button></Link>}
    >
      <ProjectList/>
    </MainPage>
  );
};
export default ProjectsPage;