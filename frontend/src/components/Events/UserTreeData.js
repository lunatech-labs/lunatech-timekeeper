import React from 'react';
import {Avatar, Table} from 'antd';
import PropTypes from 'prop-types';

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

    // rowSelection objects indicates the need for row selection
    const rowSelection = {
        onChange: (selectedRowKeys, selectedRows) => {
            console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows); //TODO
        },
        onSelect: (record, selected, selectedRows) => {
            console.log(record, selected, selectedRows); //TODO
        },
        onSelectAll: (selected, selectedRows, changeRows) => {
            console.log(selected, selectedRows, changeRows); //TODO
        },
    };

    const dataWithKey = users.map(user => {
        return {
            ...user,
            key: `event-user-row-${user.id}`
        };
    });
    return (
        <>
            <Table
                columns={columns}
                rowSelection={{rowSelection}}
                dataSource={dataWithKey}
            />
        </>
    );
};
UserTreeData.propTypes = {
    users: PropTypes.array
};

export default UserTreeData;