import React from 'react'
import { Button, PageHeader } from 'antd'

import logo from '../../logo_timekeeper_homepage.png';

const TempHeader = ({ }) => (
    <PageHeader
        title="Timekeeper"
        className="header"
        extra={[
            <a href="/home"><Button className="btn home" key="1">Home</Button></a>,
            <a href="/customers"><Button className="btn customers" key="3">Customers</Button></a>,
        ]}
        avatar={{src: logo}}
    />
);

export default TempHeader
