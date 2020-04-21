import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Menu} from 'antd';
import './MenuSidebar.less';
import PropTypes from 'prop-types';

import {DesktopOutlined, PieChartOutlined} from '@ant-design/icons';

class MenuSidebar extends Component {
  render() {
    const {location} = this.props;
    return (
      <Menu id="tk_Menu" defaultSelectedKeys={['/home']} mode="inline" selectedKeys={[location.pathname]} theme="dark">
        <Menu.Item className="tk_MenuItem" key="/home">
          <Link to="/home">
            <PieChartOutlined/>
            <span>Home</span>
          </Link>
        </Menu.Item>
        <Menu.Item className="tk_MenuItem" key="/clients">
          <Link to="/clients">
            <DesktopOutlined/>
            <span>Clients</span>
          </Link>
        </Menu.Item>
      </Menu>
    );
  }
}

MenuSidebar.propTypes = {
  location: PropTypes.object.isRequired
};

export default withRouter(MenuSidebar);