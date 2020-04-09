import React from 'react'
import { Card } from 'antd'
import { EditOutlined, EllipsisOutlined, SettingOutlined } from '@ant-design/icons';
import logo from '../../../logo_timekeeper_homepage.png';

const { Meta } = Card;

const CustomerDetails = ({ id, name }) => (
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
        title={name}
        description={`this is the item number ${id} also called ${name}`}
        />
    </Card>
);

export default CustomerDetails;