import React from 'react';
import {Alert, PageHeader, Spin} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';
import Table from "antd/es/table";

const UserList = () => {

  const usersResponse = useTimeKeeperAPI('/api/users');
  const projectsResponse = useTimeKeeperAPI('/api/projects');
  const membersResponse = useTimeKeeperAPI('/api/members');

  const userToUserData = (user) => {
    const members = membersResponse.data;
    const projects = projectsResponse.data
      .filter(project => user.projectsId.includes(project.id))
      .map(project => {
        const member = members.find(m => m.projectId === project.id && m.userId === user.id);
        return {
          ...project,
          role: member.role
        }
      });
    return {
      ...user,
      key: user.id,
      name: `${user.firstName} ${user.lastName}`,
      projects: projects
    }
  };
  const columns = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: 'Profiles',
      dataIndex: 'profiles',
      key: 'profiles',
    },
    {
      title: 'Organization',
      key: 'profiles',
      // TODO: Replace me when the organization is implemented
      // dataIndex: 'organization',
      render: () => 'REPLACE ME'
    },
    {
      title: 'Project',
      dataIndex: 'projects',
      key: 'project',
      render: (value) => value.map(v => <div key={`project-${v.name}-${v.userId}`}>{v.name}</div>)
    },
    {
      title: 'Role',
      dataIndex: 'projects',
      key: 'role',
      render: (value) => value.map(v => <div key={`role-${v.name}-${v.userId}`}>{v.role}</div>)
    }
  ];

  if (usersResponse.loading || membersResponse.loading || projectsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading</p>
        </Spin>
      </React.Fragment>

    );
  }
  if (usersResponse.error || membersResponse.error || projectsResponse.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
               message='Failed to load'
               type='error'
               description='check that the user has authorities to access these resources'
        />
      </React.Fragment>
    );
  }

  return (

    <React.Fragment>
      <PageHeader title="Clients" subTitle={usersResponse.data.length}/>
      <Table
        dataSource={usersResponse.data.map(user => userToUserData(user))}
        columns={columns}
      />
    </React.Fragment>
  );
};

export default UserList;