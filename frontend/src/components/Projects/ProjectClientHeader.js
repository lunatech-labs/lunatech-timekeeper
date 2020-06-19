import React from 'react';
import {Avatar, Divider} from 'antd';
import logo from '../../img/logo_timekeeper_homepage.png';
import TagProjectClient from '../Tag/TagProjectClient';
import PropTypes from 'prop-types';
import './ProjectClientHeader.less';

const ProjectClientHeader = ({project}) => {
  return (
    <div className='tk_ProjectClientHeader'>
      <Avatar src={logo} shape={'square'} size="large"/>
      <p>{project.name}</p>
      <Divider type="vertical"/>
      <TagProjectClient client={project.client}/>
    </div>
  );
};

ProjectClientHeader.propTypes = {
  project: PropTypes.shape({
    name: PropTypes.string,
    client: PropTypes.object
  })
};


export default ProjectClientHeader;
