import React from 'react';
import {Menu, Dropdown, Button} from 'antd';
import './HeaderProfile.less';
import {useKeycloak} from '@react-keycloak/web';
import PropTypes from 'prop-types';
import TkUserAvatar from '../Users/TkUserAvatar';

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
          <Button id="logoutBtn" type="link" onClick={() => keycloak.logout()}>Logout</Button>
        )}
      </Menu.Item>
    </Menu>
  );

  return (
    <div className="tk_Header_Profile">
      <Dropdown overlay={menu}>
        <Button id="avatarBtn" type="link" onClick={e => e.preventDefault()}>
          <TkUserAvatar picture={user.picture} name={user.name}/>
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