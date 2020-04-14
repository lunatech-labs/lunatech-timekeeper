import React from 'react';
import PropTypes from 'prop-types';
import {List, Avatar } from 'antd';
import { EditOutlined, MoreOutlined } from '@ant-design/icons';
import LinkButton from "../../atoms/LinkButton";

import './Customer.scss'

const CustomerList = ({ list, logo }) => {

    return (
        <React.Fragment>
            <List
                className="customer-list"
                itemLayout="horizontal"
                dataSource={list}
                renderItem={item => (
                    <List.Item
                        actions={[
                            <LinkButton
                                to={`/customers/${item.id}`}
                                shape="circle"
                                className="customer-edit-link"
                                icon={<EditOutlined />}
                            />,
                            <LinkButton
                                to={`/customers/${item.id}`}
                                shape="circle"
                                className="customer-more-link"
                                icon={<MoreOutlined />}
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
            <LinkButton to="/customers/new" className="btn customer-new">
                Add a customer
            </LinkButton>
        </React.Fragment>
    )
};

CustomerList.propTypes = {
    list: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number,
            name: PropTypes.string
        })
    ),
    logo: PropTypes.string
};

export default CustomerList;