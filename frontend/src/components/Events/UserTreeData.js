import React, {useState} from 'react';
import {Avatar, Table} from 'antd';
import PropTypes from 'prop-types';
import './UserTreeData.less';

const UserTreeData = ({users}) => {
  const columns = [
    {
      dataIndex: 'picture',
      key: 'picture',
      width: '1%',
      render: pictureUrl => <Avatar src={pictureUrl}/>
    },
    {
      dataIndex: 'name',
      key: 'name',
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
        scroll={{ y: 230 }}
        className="tk_UserTreeData"
      />
    </div>
  );
};
UserTreeData.propTypes = {
  users: PropTypes.array
};

export default UserTreeData;