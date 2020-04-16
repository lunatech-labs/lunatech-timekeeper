import React from 'react';
import PropTypes from 'prop-types';
import {Avatar, List} from 'antd';
import {EditOutlined, MoreOutlined} from '@ant-design/icons';
import {Link} from "react-router-dom";
import './Customer.less'

const CustomerList = ({customers, logo, activities}) => {
    const activitiesIdToActivities = (activitiesId) => {
        return activities.filter(activity => activitiesId.includes(activity.id));
    };
    const renderActivities = (activitiesId) => activitiesIdToActivities(activitiesId).map(activity => activity.name).join(" | ");
    return (
        <React.Fragment>
            <List
                className="customer-list"
                itemLayout="horizontal"
                dataSource={customers}
                renderItem={item => (
                    <List.Item
                        actions={[
                            <Link
                                to={`/customers/${item.id}`}
                                shape="circle"
                                className="customer-edit-link"
                                icon={<EditOutlined />}
                            />,
                            <Link
                                to={`/customers/${item.id}`}
                                shape="circle"
                                className="customer-more-link"
                                icon={<MoreOutlined />}
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
                        <div>{renderActivities(item.activitiesId)}</div>
                    </List.Item>
                )}
            />
            <Link to="/customers/new" className="btn customer-new" type="button">
                Add a customer
            </Link>
        </React.Fragment>
    )
};

CustomerList.propTypes = {
    customers: PropTypes.arrayOf(
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