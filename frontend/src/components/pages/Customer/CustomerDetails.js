import React from 'react'
import PropTypes from 'prop-types'
import { Card } from 'antd'
import { EditOutlined, EllipsisOutlined, SettingOutlined } from '@ant-design/icons';
import logo from '../../../img/logo_timekeeper_homepage.png';

const { Meta } = Card;

const CustomerDetails = ({ customer }) => (
    <Card
        style={{ width: 900 }}
        bordered="true"
        actions={[
        <SettingOutlined key="setting" />,
        <EditOutlined key="edit" />,
        <EllipsisOutlined key="ellipsis" />,
        ]}
    >
        <Meta
        avatar={<img
            alt="example"
            src={logo}
            style={{ width: 300 }}
        />}
        title={customer.name}
        description={`this is the customer number ${customer.id} also called ${customer.name}`}
        />
    </Card>
);

CustomerDetails.propTypes = {
    customer: PropTypes.shape({
        id: PropTypes.number,
        name: PropTypes.string
    })
};

export default CustomerDetails;