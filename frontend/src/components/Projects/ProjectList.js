import React from 'react';

import {Button, PageHeader} from 'antd';
import {Link} from 'react-router-dom';
import PlusOutlined from '@ant-design/icons/lib/icons/PlusOutlined';
import './ProjectList.less';


const ProjectList = () => {
  return (
    <React.Fragment>
      <div className="tk_TopPage">
        <PageHeader title="Projects"/>
        <div id="add-button">
          <Link key='addLink' to='/projects/new'><Button type="primary" icon={<PlusOutlined/>}>Add a project</Button></Link>
        </div>
      </div>
    </React.Fragment>
  );
};
export default ProjectList;
