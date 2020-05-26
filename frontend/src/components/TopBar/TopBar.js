import React, {useState} from 'react';
import {MenuUnfoldOutlined, MenuFoldOutlined, BellOutlined} from '@ant-design/icons';
import {Layout, Avatar, Badge} from 'antd';
import './TopBar.less';
import PropTypes from 'prop-types';
import OrganizationPictureFR from '../../img/organization_icon_fr.png';
import OrganizationPictureNL from '../../img/organization_icon_nl.png';
import HeaderProfile from './HeaderProfile';

const { Header } = Layout;

const TopBar = ({ collapsed, toggle, user }) => {
  const [alert, setAlert] = useState(true);

  const alertChangeState = () => {
    setAlert(!alert);
  };

  const displayOrganizationPicture = (email) => {
    if(email.includes('@lunatech.fr')){
      return OrganizationPictureFR;
    } else if(email.includes('@lunatech.nl')) {
      return OrganizationPictureNL;
    } else if(email.includes('@lunatech.com')) {
      return OrganizationPictureNL;
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
        <HeaderProfile user={user} />
        <Avatar shape="square" size="large" src={displayOrganizationPicture(user.email)} />
      </div>
    </Header>
  );
};

TopBar.propTypes = {
  collapsed: PropTypes.bool.isRequired,
  toggle: PropTypes.func.isRequired,
};

export default TopBar;