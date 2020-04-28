import React from 'react';
import {Alert, Avatar, PageHeader, Spin, Table} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';

const UserList = () => {

  const usersResponse = useTimeKeeperAPI('/api/users');
  const projectsResponse = useTimeKeeperAPI('/api/projects');

  const userToUserData = (user) => {
    const projects = user.memberOfprojects
      .map(member => {
        const project = projectsResponse.data
          .filter(project => project.publicAccess === false)
          .find(project => member.projectId === project.id);
        return project ?
          {
            ...project,
            role: member.role
          } : undefined
      }).filter(p => !!p);
    console.log(projects)
    return {
      ...user,
      key: user.id,
      name: `${user.firstName} ${user.lastName}`,
      projects: projects
    };
  };
  const columns = [
    {
      title: '',
      dataIndex: 'picture',
      key: 'picture',
      width: 60,
      align: 'right',
      render: (value) => <Avatar src={value} />
    },
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
      render: (value) => value.join(', ')
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

  if (usersResponse.loading || projectsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading</p>
        </Spin>
      </React.Fragment>

    );
  }
  if (usersResponse.error || projectsResponse.error) {
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
      <PageHeader title="Users" subTitle={usersResponse.data.length}/>
      <Table
        dataSource={usersResponse.data.map(user => userToUserData(user))}
        columns={columns}
      />
    </React.Fragment>
  );
};

export default UserList;