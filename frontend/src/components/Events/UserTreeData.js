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

import React, {useState} from 'react';
import {Avatar, Table} from 'antd';
import PropTypes from 'prop-types';
import './UserTreeData.less';

const UserTreeData = ({users, usersSelected, setUsersSelected}) => {
  const renderAvatar = (pictureUrl) => {
    return <Avatar src={pictureUrl}/>;
  };

  const columns = [
    {
      dataIndex: 'picture',
      key: 'picture',
      width: '10%',
      render: pictureUrl => renderAvatar(pictureUrl)
    },
    {
      dataIndex: 'name',
      key: 'name',
      width: '80%',
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