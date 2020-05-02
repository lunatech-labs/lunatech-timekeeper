import React from 'react';
import {Breadcrumb, Typography, Alert, Avatar, Spin, Table} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';
import './UserList.less';
import CheckCircleFilled from '@ant-design/icons/es/icons/CheckCircleFilled';

const { Title } = Typography;

const UserList = () => {
  const usersResponse = useTimeKeeperAPI('/api/users');

  const userToUserData = (user) => {
    return {
      ...user,
      key: user.id,
      name: `${user.firstName} ${user.lastName}`
    };
  };

  // local component created to avoid an es-lint error
  const renderAvatar = (value) => <Avatar src={value} />;

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
      title: 'Project',
      dataIndex: 'projects',
      key: 'project',
      render: (value) => value.map(v => <div key={`project-${v.name}-${v.userId}`}>{v.name}</div>)
    },
    {
      title: 'Manager',
      dataIndex: 'projects',
      key: 'manager',
      align: 'center',
      render: (value) => value.map(v => <div key={`role-${v.name}-${v.userId}`}>{value ? <CheckCircleFilled style={{ fontSize: '18px'}} /> : '' }</div>)
    }
  ];

  if (usersResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading</p>
        </Spin>
      </React.Fragment>
    );
  }
  if (usersResponse.error) {
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
    <div>
      <div className="tk_TopPage">
        <Breadcrumb id="tk_Breadcrumb">
          <Breadcrumb.Item>
            <a href="">Home</a>
          </Breadcrumb.Item>
          <Breadcrumb.Item>Users</Breadcrumb.Item>
        </Breadcrumb>
        <Title>List of users</Title>
        <div>
          <p>{usersResponse.data.length} Users</p>
          <div>
            <p>TO DO</p>
          </div>
        </div>
      </div>
      <Table id="tk_Table"
        dataSource={usersResponse.data.map(user => userToUserData(user))}
        columns={columns}
      />
    </div>
  );
};

export default UserList;