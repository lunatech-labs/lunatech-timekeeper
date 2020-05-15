import React from 'react';
import {Avatar, Col, Row, Tag, Typography} from 'antd';
import './ShowProject.less';
import PropTypes from 'prop-types';
import {
  DesktopOutlined,
  DollarOutlined,
  UserOutlined,
  LockOutlined,
} from '@ant-design/icons';
import TitleSection from '../Title/TitleSection';
import CardLg from '../Card/CardLg';
import CardMember from '../Card/CardMember';
import TagMember from '../Tag/TagMember';

const {Title} = Typography;

const ShowProject = ({project}) => {

  const users = () => {
    if (!project.users || project.users.length === 0) {
      return 'No user added yet';
    } else {
      const users = project.users.sort((u1, u2) => {
        const res = u2.manager - u1.manager;
        return res === 0 ? u1.name.localeCompare(u2.name) : res;
      });
      return users.map(user =>
        <CardMember key={`project-member-${user.id}`}>
          <div>
            <Avatar src={user.picture}/>
            <p>{user.name}</p>
          </div>
          <TagMember isManager={user.manager} />
        </CardMember>
      );
    }
  };

  return (
    <div>
      <CardLg>
        <Title level={2}>{project.name}</Title>
        <Row gutter={32}>
          <Col span={12}>
            <TitleSection title="Information"/>
            <Row gutter={32}>
              <Col span={12}>
                <p className="tk_ProjectAtt"><DesktopOutlined/> Client : {project.client ?<Tag color="blue">{project.client.name}</Tag> : <span className="tk_Project_Client_Tag">No client</span>}</p>
                <p className="tk_ProjectAtt"><UserOutlined/> Members : {project.users ? project.users.length : 0}</p>
              </Col>
              <Col span={12}>
                <p className="tk_ProjectAtt"><DollarOutlined/> Billable : {project.billable ? 'Yes' : 'No'}</p>
                <p className="tk_ProjectAtt"><LockOutlined/> Project type : {project.publicAccess ? 'Public' : 'Private'}</p>
              </Col>
            </Row>
            <Col span={24}>
              <p className="tk_ProjectDesc">{project.description}</p>
            </Col>
          </Col>
          <Col span={12}>
            <TitleSection title="Members"/>
            {users()}
          </Col>
        </Row>
      </CardLg>
    </div>
  );
};

ShowProject.propTypes = {
  project: PropTypes.object.isRequired
};

export default ShowProject;