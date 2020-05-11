import React from 'react';
import './ProjectMemberTag.less';
import Tag from 'antd/lib/tag';
import PropTypes from 'prop-types';
import {Avatar} from "antd";
import logo from "../../img/logo_timekeeper_homepage.png";
import Space from "antd/lib/space";


const ProjectMemberTag = ({ member }) => {
  if(member.isManager) {
    return (
      <React.Fragment>
        <Avatar src={member.picture} shape={'square'} size="large"/> {member.picture}  <Tag className="managerTag">Team leader</Tag>
      </React.Fragment>
    );
  }else{
    return (
      <React.Fragment>
        <Avatar src={member.picture} shape={'square'} size="large"/>   <Tag className="userTag">Member</Tag>
      </React.Fragment>
    );
  }
};

ProjectMemberTag.propTypes = {
  memberName: PropTypes.object.isRequired

};

export default ProjectMemberTag;