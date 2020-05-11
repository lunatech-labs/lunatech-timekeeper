import React from "react";
import {Avatar, Col, Descriptions, Divider, Row, Table, Tag, Typography} from "antd";
import PropTypes from "prop-types";
import {DesktopOutlined, UserOutlined} from "@ant-design/icons";
import DollarOutlined from "@ant-design/icons/lib/icons/DollarOutlined";
import LockOutlined from "@ant-design/icons/lib/icons/LockOutlined";
import TitleSection from "../Title/TitleSection";

const renderAvatar = (value) => <Avatar src={value}/>;

const {Title} = Typography;

const columns = [
  {
    title: '',
    dataIndex: 'picture',
    key: 'picture',
    width: 60,
    align: 'right',
    render: (value) => renderAvatar(value),
  },
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: 'Manager',
    dataIndex: 'manager',
    key: 'manager',
    render: (value) => value ? "Manager" : "Member"
  }
];

const ShowProject = ({project}) => {

  const users = project.users.map(user => {
    return (
      <Row>
        <Col span={2}>
          <Avatar src={user.picture}/>
        </Col>
        <Col span={14}>
          {user.name}
        </Col>
        <Col span={6}>
          {user.manager ? <Tag color="gold">Team Leader</Tag> : <Tag color="grey">Member</Tag>}
        </Col>
      </Row>
    )
  });

  return (
    <div>
      <Title level={3}>{project.name}</Title>
    <Row gutter={16}>
      <Col className="gutter-row" span={12}>
        <TitleSection title="Information"/>

        <Row gutter={16}>
          <Col span={12}><DesktopOutlined /> Client : {project.client ? project.client.name : 'No client'}</Col>
          <Col span={12}><DollarOutlined /> Billable : {project.billable ? 'Yes' : 'No'}</Col>
        </Row>
        <Row gutter={16}>
          <Col span={12}><UserOutlined /> Members : {project.users ? project.users.length : 0}</Col>
          <Col span={12}><LockOutlined /> Project type : {project.publicAccess ? 'Public' : 'Private'}</Col>
        </Row>
        <Row gutter={16}>
        <div>
          {project.description}
        </div>
        </Row>
      </Col>
      <Col className="gutter-row" span={12}>
        <TitleSection title="Members"/>
        {users}
      </Col>
    </Row>
    </div>
  );
};

ShowProject.propTypes = {
  project: PropTypes.object.isRequired
};

export default ShowProject;