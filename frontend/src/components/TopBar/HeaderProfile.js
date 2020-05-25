import React from 'react';
import {Avatar, Menu, Dropdown} from 'antd';
import './HeaderProfile.less';
import {useKeycloak} from '@react-keycloak/web';
import PropTypes from "prop-types";

const HeaderProfile = ({ user }) => {

  const displayProfile = (profiles) => {
    if(profiles.includes('Admin')){
      return 'Admin';
    } else {
      return 'User';
    }
  };

  const { keycloak } = useKeycloak();

  const menu = (
    <Menu>
      <Menu.Item key="1">
        {!!keycloak.authenticated && (
          <a onClick={() => keycloak.logout()}>Logout</a>
        )}
        </Menu.Item>
    </Menu>
  );

  return (
    <div className="tk_Header_Profile">
      <Dropdown overlay={menu}>
        <a onClick={e => e.preventDefault()}>
          <Avatar src={user.picture} />
          <p>{user.name}<br/><span>{displayProfile(user.profiles)}</span></p>
        </a>
      </Dropdown>
    </div>
  );
};

HeaderProfile.propTypes = {
  user: PropTypes.object.isRequired
};

export default HeaderProfile;