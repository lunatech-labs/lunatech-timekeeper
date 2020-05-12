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
        <Tag id="tk_Tag" className="tk_Tag_Gold">Team leader</Tag>
      </React.Fragment>
    );
  }else{
    return (
      <React.Fragment>
        <Avatar src={member.picture} shape={'square'} size="large"/>
        <Tag id="tk_Tag">Member</Tag>
      </React.Fragment>
    );
  }
};

ProjectMemberTag.propTypes = {
  memberName: PropTypes.object.isRequired

};

export default ProjectMemberTag;