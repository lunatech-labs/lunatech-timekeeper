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

const UserTreeData = ({users, usersSelected, setUsersSelected, eventType}) => {
  const renderUser = (id, name, picture) => {
    return (
      <div key={`user-row-tree-data-${id}`}>
        <TkUserAvatar name={name} picture={picture}/>
        <span id="tk_User_Name">{name}</span>
      </div>
    );
  };

  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [rowSelectionType, setRowSelectionType] = useState('checkbox');

  useEffect(() => {
    if(eventType){
      (eventType === 'userEvent') ? setRowSelectionType('radio') : setRowSelectionType('checkbox');
    }
  }, [eventType]);


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
      render: name => users.filter(user => user.name === name).map(user => renderUser(user.id, user.name, user.picture))
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
      {(eventType === 'userEvent')?
        '' :
        <div>
          <span style={{ marginLeft: 8 }}>
            {hasSelected !== 0 ? `${hasSelected} selected` : ''}
          </span>
          <span id="tk_Select_All_Text">Select All</span>
        </div>
      }

      <Table
        columns={columns}
        rowSelection={{
          type: rowSelectionType,
          ...rowSelection
        }}
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
  setUsersSelected: PropTypes.func.isRequired,
  eventType: PropTypes.string.isRequired
};

export default UserTreeData;