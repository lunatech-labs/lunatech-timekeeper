import React from 'react';
import './ProjectMemberTag.less';
import PropTypes from 'prop-types';
import TagMember from '../Tag/TagMember';
import CardXs from '../Card/CardXs';
import TkUserAvatar from '../Users/TkUserAvatar';

const ProjectMemberTag = ({ member }) => {
  return (
    <CardXs>
      <div>
        <TkUserAvatar picture={member.picture} name={member.name}/>
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