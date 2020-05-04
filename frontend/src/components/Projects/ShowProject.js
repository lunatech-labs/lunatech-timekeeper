import React from "react";
import {Avatar, Descriptions, Table} from "antd";
import PropTypes from "prop-types";

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

  return (
    <React.Fragment>
      <Descriptions title="Project information"
                    bordered
                    column={{xxl: 1, xl: 1, lg: 1, md: 1, sm: 1, xs: 1}}>
        <Descriptions.Item label="Name">{project.name}</Descriptions.Item>
        <Descriptions.Item label="Organization">TODO</Descriptions.Item>
        <Descriptions.Item label="Description">{project.description}</Descriptions.Item>
        <Descriptions.Item label="Client">{project.client.name}</Descriptions.Item>
        <Descriptions.Item label="Billable">{project.billable ? 'Yes' : 'No'}</Descriptions.Item>
        <Descriptions.Item
          label="Project type">{project.publicAccess ? 'Public project' : 'Private project'}</Descriptions.Item>
        <Descriptions.Item label="Users in the projects">
          <Table id="tk_Table"
                 dataSource={project.users}
                 columns={columns}
          />
        </Descriptions.Item>
      </Descriptions>
    </React.Fragment>
  );
};

ShowProject.propTypes = {
  project: PropTypes.object.isRequired
};

export default ShowProject;