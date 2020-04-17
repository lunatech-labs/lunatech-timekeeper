import React from 'react';
import PropTypes from 'prop-types';
import {Avatar, List} from 'antd';
import {EditOutlined, MoreOutlined} from '@ant-design/icons';
import {Link} from "react-router-dom";
import './Client.less'

const ClientList = ({clients, logo, activities}) => {
    const activitiesIdToActivities = (activitiesId) => {
        return activities.filter(activity => activitiesId.includes(activity.id));
    };
    const renderActivities = (activitiesId) => activitiesIdToActivities(activitiesId).map(activity => activity.name).join(" | ");
    return (
        <React.Fragment>
            <List
                itemLayout="horizontal"
                dataSource={clients}
                renderItem={item => (
                    <List.Item
                        actions={[
                            <Link
                                to={`/clients/${item.id}`}
                                shape="circle"
                                icon={<EditOutlined />}
                            />,
                            <Link
                                to={`/clients/${item.id}`}
                                shape="circle"
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
            <Link to="/clients/new" type="primary" >
                Add a Client
            </Link>
        </React.Fragment>
    )
};

ClientList.propTypes = {
    clients: PropTypes.arrayOf(
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

export default ClientList;