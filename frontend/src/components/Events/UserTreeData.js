/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useEffect, useState} from 'react';
import {Table} from 'antd';
import PropTypes from 'prop-types';
import './UserTreeData.less';
import TkUserAvatar from '../Users/TkUserAvatar';

const UserTreeData = ({users, usersSelected, setUsersSelected}) => {
  const renderUser = (name, picture) => {
    return (
      <div>
        <TkUserAvatar name={name} picture={picture}/>
        <span id="tk_User_Name">{name}</span>
      </div>
    );
  };

  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  useEffect(()=> {
    if(usersSelected){
      setSelectedRowKeys(usersSelected.map(user => `event-user-row-${user.userId}`));
    }
  }, [usersSelected]);

  const columns = [
    {
      dataIndex: 'name',
      key: 'name',
      width: '100%',
      render: name => users.filter(user => user.name === name).map(user => renderUser(user.name, user.picture))
    }
  ];

  const dataWithKey = users.map(user => {
    return {
      ...user,
      key: `event-user-row-${user.id}`
    };
  });

  const [hasSelected, setHasSelected] = useState(0);

  // rowSelection objects indicates the need for row selection
  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys, selectedRows) => {
      setHasSelected(selectedRows.length);
      setUsersSelected(selectedRows.map(user => {
        return {'userId' :user.id};
      }));
    },
  };
  return (
    <div>
      <span style={{ marginLeft: 8 }}>
        {hasSelected !== 0 ? `${hasSelected} selected` : ''}
      </span>
      <span id="tk_Select_All_Text">Select All</span>
      <Table
        columns={columns}
        rowSelection={{...rowSelection}}
        dataSource={dataWithKey}
        scroll={{ y: 240 }}
        pagination={false}
        className="tk_UserTreeData"
      />
    </div>
  );
};
UserTreeData.propTypes = {
  users: PropTypes.array.isRequired,
  usersSelected: PropTypes.array.isRequired,
  setUsersSelected: PropTypes.func.isRequired
};

export default UserTreeData;