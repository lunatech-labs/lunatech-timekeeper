import React from "react";
import {Avatar, Col, Descriptions, Divider, Row, Table, Tag} from "antd";
import PropTypes from "prop-types";
import {DesktopOutlined, UserOutlined} from "@ant-design/icons";
import DollarOutlined from "@ant-design/icons/lib/icons/DollarOutlined";
import LockOutlined from "@ant-design/icons/lib/icons/LockOutlined";
import TitleSection from "../Title/TitleSection";

const renderAvatar = (value) => <Avatar src={value}/>;

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
        <Col span={10}>
          <Avatar src={user.picture}/> {user.name}
        </Col>
        <Col span={6}>
          {user.manager ? <Tag color="gold">Team Leader</Tag> : <Tag color="grey">Member</Tag>}
        </Col>
      </Row>
    )
  });

  return (
    <Row gutter={16}>
      <Col className="gutter-row" span={12}>
        <p className="tk_FormTitle">Informations</p>
        <Row gutter={16}>
        <Col span={8}>
          <div><DesktopOutlined /> Client : {project.client ? project.client.name : 'No client'}</div>
          <div><UserOutlined /> Members : {project.users ? project.users.length : 0}</div>
        </Col>
        <Col span={8}>
          <div><DollarOutlined /> Billable : {project.publicAccess ? 'Public project' : 'Private project'}</div>
          <div><LockOutlined /> Project type : {project.publicAccess ? 'Public project' : 'Private project'}</div>
        </Col>
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
  );
};

ShowProject.propTypes = {
  project: PropTypes.object.isRequired
};

export default ShowProject;