import React, { Component } from 'react';
import { Descriptions } from 'antd';

class ShowUser extends Component {
    render() {
        return (
            <Descriptions title="User Info"
                          bordered
                          column={{ xxl: 1, xl: 1, lg: 1, md: 1, sm: 1, xs: 1 }}>
                <Descriptions.Item label="UserName">{this.props.user.name}</Descriptions.Item>
                <Descriptions.Item label="First name"> {this.props.user.givenName}</Descriptions.Item>
                <Descriptions.Item label="Last name"> {this.props.user.familyName}</Descriptions.Item>
                <Descriptions.Item label="Email"> {this.props.user.email}</Descriptions.Item>
            </Descriptions>
    );
    }

}
export default ShowUser


