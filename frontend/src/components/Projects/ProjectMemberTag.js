import React from 'react';
import './ProjectMemberTag.less';
import PropTypes from 'prop-types';
import {Avatar} from 'antd';
import TagMember from '../Tag/TagMember';
import CardXs from '../Card/CardXs';

const ProjectMemberTag = ({ member }) => {
  return (
    <CardXs>
      <div>
        <Avatar src={member.picture}/>
        <p>{member.name}</p>
      </div>
      <TagMember isManager={member.manager} />
    </CardXs>
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