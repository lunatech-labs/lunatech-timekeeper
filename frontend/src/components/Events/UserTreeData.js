import React, {useState} from 'react';
import {Avatar, Table} from 'antd';
import PropTypes from 'prop-types';
import './UserTreeData.less';

const UserTreeData = ({users}) => {
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
  users: PropTypes.array
};

export default UserTreeData;