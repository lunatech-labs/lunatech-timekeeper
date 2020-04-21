import React from 'react';
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
} from '@ant-design/icons';
import {Layout} from 'antd';
import './TopBar.less';
import PropTypes from 'prop-types';

const { Header } = Layout;

const TopBar = ({ collapsed, toggle }) => {
  return (
    <Header className="site-layout-background" style={{ padding: 0 }}>
      {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
        className: 'trigger',
        onClick: toggle,
      })}
    </Header>
  );
};

TopBar.propTypes = {
  collapsed: PropTypes.bool.isRequired,
  toggle: PropTypes.func.isRequired
};

export default TopBar;