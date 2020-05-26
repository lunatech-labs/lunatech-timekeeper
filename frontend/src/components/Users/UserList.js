import React from 'react';
import {Alert, Avatar, Spin, Table} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';
import './UserList.less';
import Button from 'antd/lib/button';
import TagMember from '../Tag/TagMember';

const UserList = () => {
  const usersResponse = useTimeKeeperAPI('/api/users');

  const userToUserData = (user) => {
    return {
      ...user,
      key: user.id
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
      title: 'Role',
      dataIndex: 'projects',
      key: 'projects',
      render: (value) => value.map(v => <div key={`role-${v.name}-${v.userId}`}>{<TagMember isManager={v.manager} />}</div>)
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

  let paginationItemRender = (current, type) => {
    if (type === 'prev') {
      return <Button type="primary" shape="circle">&lt;</Button>;
    } else if (type === 'next') {
      return <Button type="primary" shape="circle">&gt;</Button>;
    } else {
      return <Button type="primary" shape="circle">{current}</Button>;
    }
  };

  return (
    <React.Fragment>
      <div className="tk_SubHeader">
        <p>{usersResponse.data.length} Users</p>
        <div className="tk_SubHeader_RightPart">
        </div>
      </div>

      <Table id="tk_Table"
        dataSource={usersResponse.data.map(user => userToUserData(user))}
        columns={columns} pagination={{ position:['bottomCenter'], pageSize:20, hideOnSinglePage:true, itemRender: paginationItemRender }}
      />
    </React.Fragment>
  );
};

export default UserList;