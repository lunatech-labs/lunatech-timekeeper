import React, {useState} from 'react';
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  BellOutlined,
} from '@ant-design/icons';
import {Layout, Avatar, Badge, Col, Row} from 'antd';
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
    <Header id="tk_Header" className="site-layout-background" style={{ padding: 0 }}>
      <Row>
        <Col flex={7}>
          {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
            className: 'trigger',
            onClick: toggle,
          })}
        </Col>
        <Col flex={3}>
          <Row gutter={[18,18]}>
            <Col span={2}>
              <Badge dot={alert} onClick={alertChangeState}>
                <BellOutlined />
              </Badge>
            </Col>
            <Col span={9}>
              <Row gutter={[6,6]}>
                <Col>
                  <Avatar src={user.picture} />
                </Col>
                <Col>
                  <div>{user.name}</div>
                  <div>{displayProfile(user.profiles)}</div>
                </Col>
              </Row>
            </Col>
            <Col span={2}>
              <Avatar shape="square" size="large" src={displayOrgaPicture(user.email)} />
            </Col>
          </Row>
        </Col>
      </Row>
    </Header>
  );
};

TopBar.propTypes = {
  collapsed: PropTypes.bool.isRequired,
  toggle: PropTypes.func.isRequired,
  user: PropTypes.object.isRequired
};

export default TopBar;