import React from 'react';
import {Avatar, Menu, Dropdown, Button} from 'antd';
import './HeaderProfile.less';
import {useKeycloak} from '@react-keycloak/web';
import PropTypes from 'prop-types';

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
          <Button type="link" onClick={() => keycloak.logout()}>Logout</Button>
        )}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className="tk_Header_Profile">
      <Dropdown overlay={menu}>
        <Button type="link" onClick={e => e.preventDefault()}>
          <Avatar src={user.picture} />
          <p>{user.name}<br/><span>{displayProfile(user.profiles)}</span></p>
        </Button>
      </Dropdown>
    </div>
  );
};

HeaderProfile.propTypes = {
  user: PropTypes.object.isRequired
};

export default HeaderProfile;