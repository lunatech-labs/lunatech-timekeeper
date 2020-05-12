import React from 'react';
import './ProjectMemberTag.less';
import Tag from 'antd/lib/tag';
import PropTypes from 'prop-types';
import {Avatar} from 'antd';

const ProjectMemberTag = ({ member }) => {
  if(member.manager) {
    return (
      <React.Fragment>
        <Avatar src={member.picture} shape={'square'} size="large"/>
        {member.name}
        <Tag id="tk_Tag" className="tk_Tag_Gold">Team leader</Tag>
      </React.Fragment>
    );
  }else{
    return (
      <React.Fragment>
        <Avatar src={member.picture} shape={'square'} size="large"/>
        {member.name}
        <Tag id="tk_Tag">Member</Tag>
      </React.Fragment>
    );
  }
};

ProjectMemberTag.propTypes = {
  member: PropTypes.shape({
    name: PropTypes.string.isRequired,
    picture: PropTypes.string,
    manager: PropTypes.boolean
  })
};

export default ProjectMemberTag;