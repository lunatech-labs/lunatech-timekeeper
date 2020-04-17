import React from 'react';
import PropTypes from 'prop-types';
import {Avatar, List} from 'antd';
import {Link} from "react-router-dom";
import './Customer.less'

const CustomerList = ({customers, logo, activities}) => {
    const activitiesIdToActivities = (activitiesId) => {
        return activities.filter(activity => activitiesId.includes(activity.id));
    };
    const renderActivities = (activitiesId) => activitiesIdToActivities(activitiesId).map(activity => activity.name).join(" | ");
    return (
        <React.Fragment>
            <Link to="/customers/new" type="primary" >
                Add a customer
            </Link>
            <List
                className="customer-list"
                itemLayout="horizontal"
                dataSource={customers}
                renderItem={item => (
                    <List.Item
                        actions={[
                            <Link to={`/customers/${item.id}`} type="primary">
                                Edit
                            </Link>
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