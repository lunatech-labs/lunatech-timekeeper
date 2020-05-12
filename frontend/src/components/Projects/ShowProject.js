import React from 'react';
import {Avatar, Col, Row, Tag, Typography} from 'antd';
import PropTypes from 'prop-types';
import {DesktopOutlined, UserOutlined} from '@ant-design/icons';
import DollarOutlined from '@ant-design/icons/lib/icons/DollarOutlined';
import LockOutlined from '@ant-design/icons/lib/icons/LockOutlined';
import TitleSection from '../Title/TitleSection';
import CardLg from '../Card/CardLg';

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
      //TODO : Use the custom card here
      return users.map(user =>
        <Row key={`project-member-${user.id}`}>
          <Col span={2}>
            <Avatar src={user.picture}/>
          </Col>
          <Col span={14}>
            {user.name}
          </Col>
          <Col span={6}>
            {user.manager ? <Tag color="gold">Team Leader</Tag> : <Tag color="grey">Member</Tag>}
          </Col>
        </Row>);
    }
  };

  return (
    <div>
      <CardLg>
        <Title level={2}>{project.name}</Title>
        <Row gutter={16}>
          <Col className="gutter-row" span={12}>
            <TitleSection title="Information"/>

            <Row gutter={16}>
              <Col span={12}><DesktopOutlined/> Client : {project.client ?
                <Tag color="blue">{project.client.name}</Tag> : 'No client'}</Col>
              <Col span={12}><DollarOutlined/> Billable : {project.billable ? 'Yes' : 'No'}</Col>
            </Row>
            <Row gutter={16}>
              <Col span={12}><UserOutlined/> Members : {project.users ? project.users.length : 0}</Col>
              <Col span={12}><LockOutlined/> Project type : {project.publicAccess ? 'Public' : 'Private'}</Col>
            </Row>
            <Row gutter={16}>
              <div>
                {project.description}
              </div>
            </Row>
          </Col>
          <Col className="gutter-row" span={12}>
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