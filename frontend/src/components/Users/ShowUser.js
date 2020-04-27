import React, { Component } from 'react';
import { Descriptions, Avatar } from 'antd';
import PropTypes from 'prop-types';

class ShowUser extends Component {

  render() {
    return (
      <Descriptions title="User Info"
        bordered
        column={{ xxl: 1, xl: 1, lg: 1, md: 1, sm: 1, xs: 1 }}>
        <Descriptions.Item label="Avatar"><Avatar src={this.props.user.picture} /></Descriptions.Item>
        <Descriptions.Item label="First name"> {this.props.user.firstName}</Descriptions.Item>
        <Descriptions.Item label="Last name"> {this.props.user.lastName}</Descriptions.Item>
        <Descriptions.Item label="Email"> {this.props.user.email}</Descriptions.Item>
        <Descriptions.Item label="Profiles"> {this.props.user.profiles.join(', ')}</Descriptions.Item>
      </Descriptions>
    );
  }

}

ShowUser.propTypes = {
  user: PropTypes.object.isRequired
};

export default ShowUser;
