import React, {useState} from 'react';
import {Alert, AutoComplete, Spin, Table} from 'antd';
import {sortListByName, useTimeKeeperAPI} from '../../utils/services';
import './UserList.less';
import Button from 'antd/lib/button';
import TagMember from '../Tag/TagMember';
import TkUserAvatar from './TkUserAvatar';
import Input from 'antd/lib/input';
import SearchOutlined from '@ant-design/icons/lib/icons/SearchOutlined';

const UserList = () => {
  const usersResponse = useTimeKeeperAPI('/api/users');

  const [value, setValue] = useState('');
  const onSearch = searchText => setValue(searchText);

  const userToUserData = (user) => {
    return {
      ...user,
      key: user.id
    };
  };

  // local component created to avoid an es-lint error
  const renderAvatar = (user) => <TkUserAvatar name={user.name} picture={user.picture} />;

  const usersFiltered = (listOfUsers) => {
    return listOfUsers.filter(user => user.name.toLowerCase().includes(value.toLowerCase()) ||
        user.email.toLowerCase().includes(value.toLowerCase()) ||
        user.projects.filter(project => project.name.toLowerCase().includes(value.toLowerCase())).length > 0
    );
  };

  const columns = [
    {
      title: '',
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
          <div className="tk_Search_Input">
            <AutoComplete onSearch={onSearch}>
              <Input data-cy="searchUserBox" size="large" placeholder="Search in users..." allowClear  prefix={<SearchOutlined />} />
            </AutoComplete>
          </div>
        </div>
      </div>

      <Table id="tk_Table"
        dataSource={usersFiltered(sortListByName(usersResponse.data.map(user => userToUserData(user))))}
        columns={columns} pagination={{ position:['bottomCenter'], pageSize:20, hideOnSinglePage:true, itemRender: paginationItemRender }}
      />
    </React.Fragment>
  );
};

export default UserList;