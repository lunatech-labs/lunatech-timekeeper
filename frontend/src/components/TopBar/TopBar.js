import React, {useState} from 'react';
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  BellOutlined,
} from '@ant-design/icons';
import {Layout, Avatar, Badge} from 'antd';
import './TopBar.less';
import PropTypes from 'prop-types';
import OrgaPictureFR from '../../img/organization_icon_fr.png';
import OrgaPictureNL from '../../img/organization_icon_nl.png';

const { Header } = Layout;

const TopBar = ({ collapsed, toggle, user }) => {
  const [alert, setAlert] = useState(true);

  const alertChangeState = () => {
    setAlert(!alert);
  };

  const displayProfile = (profiles) => {
    if(profiles.length == 1){
      return profiles;
    } else if(profiles.includes('Admin')){
      return profiles[profiles.indexOf('Admin')];
    } else {
      return profiles[profiles.indexOf('User')];
    }
  };

  const displayOrgaPicture = (email) => {
    if(email.split('@').includes('lunatech.fr')){
      return OrgaPictureFR;
    } else if(email.split('@').includes('lunatech.nl')) {
      return OrgaPictureNL;
    } else if(email.split('@').includes('lunatech.com')) {
      return OrgaPictureNL;
    } else {
      return '';
    }
  };

  return (
    <Header id="tk_Header" className="site-layout-background">
      {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
        className: 'trigger',
        onClick: toggle,
      })}
      <div className="tk_Header_Right">
        <div className="tk_Header_Notif">
          <Badge dot={alert} onClick={alertChangeState}>
            <BellOutlined />
          </Badge>
        </div>
        <div className="tk_Header_Profile">
          <Avatar src={user.picture} />
          <p>{user.name}<br/><span>{displayProfile(user.profiles)}</span></p>
        </div>

        <Avatar shape="square" size="large" src={displayOrgaPicture(user.email)} />
      </div>

    </Header>
  );
};

TopBar.propTypes = {
  collapsed: PropTypes.bool.isRequired,
  toggle: PropTypes.func.isRequired,
  user: PropTypes.object.isRequired
};

export default TopBar;