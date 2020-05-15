import React from 'react';
import './ProjectMemberTag.less';
import PropTypes from 'prop-types';
import {Avatar} from 'antd';
import TagMember from '../Tag/TagMember';

const ProjectMemberTag = ({ member }) => {
  return (
    <React.Fragment>
      <Avatar src={member.picture} shape={'square'} size="large"/>
      {member.name}
      <TagMember isManager={member.manager} />
    </React.Fragment>
  );
};

ProjectMemberTag.propTypes = {
  member: PropTypes.shape({
    name: PropTypes.string.isRequired,
    picture: PropTypes.string,
    manager: PropTypes.bool.isRequired
  })
};

export default ProjectMemberTag;