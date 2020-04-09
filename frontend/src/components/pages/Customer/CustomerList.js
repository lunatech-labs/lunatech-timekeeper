import React, {useState} from 'react';
import PropTypes from 'prop-types';
import { List, Avatar, Button } from 'antd';
import { EditOutlined, MoreOutlined } from '@ant-design/icons';

import './Customer.scss'

const CustomerList = ({ list, logo }) => {
    const [customer, setCustomer] = useState(false);
    return (
        <List
            className="customer-list"
            itemLayout="horizontal"
            dataSource={list}
            renderItem={item => (
                <List.Item
                    actions={[
                        <Button
                            shape="circle"
                            className="customer-edit-link"
                            icon={<EditOutlined />}
                            onClick={setCustomer}
                        />,
                        <Button
                            shape="circle"
                            className="customer-more-link"
                            icon={<MoreOutlined />}
                            onClick={setCustomer}
                        />
                    ]}
                >
                    <List.Item.Meta
                        avatar={
                            <Avatar src={logo} />
                        }
                        title={item.name}
                        description="is a great user ..."
                    />
                    <div> something </div>
                </List.Item>
            )}
        />
    )
};

CustomerList.propTypes = {
    list: PropTypes.array,
    logo: PropTypes.string
};

export default CustomerList;