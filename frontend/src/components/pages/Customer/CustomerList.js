import React from 'react';
import PropTypes from 'prop-types';
import {Avatar, List} from 'antd';
import {EditOutlined, MoreOutlined} from '@ant-design/icons';
import LinkButton from "../../atoms/LinkButton";

import './Customer.scss'

const CustomerList = ({list, logo, activities}) => {
    const activitiesIdToActivities = (activitiesId) => {
        return activities.filter(activity => activitiesId.includes(activity.id)).map(activity => activity.name);
    };
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
                                icon={<EditOutlined/>}
                            />,
                            <LinkButton
                                to={`/customers/${item.id}`}
                                shape="circle"
                                className="customer-more-link"
                                icon={<MoreOutlined/>}
                            />
                        ]}
                    >
                        <List.Item.Meta
                            avatar={
                                <Avatar src={logo}/>
                            }
                            title={item.name}
                            description={item.description}
                        />
                        <div>{activitiesIdToActivities(item.activitiesId).join(" | ")}</div>
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
            name: PropTypes.string,
            activitiesId: PropTypes.arrayOf(PropTypes.number)
        })
    ),
    logo: PropTypes.string,
    activities: PropTypes.arrayOf(PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string
    }))
};

export default CustomerList;