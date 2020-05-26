import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Menu} from 'antd';
import './MenuSidebar.less';
import PropTypes from 'prop-types';

import {UserOutlined, DesktopOutlined, PieChartOutlined} from '@ant-design/icons';
import FolderOpenOutlined from '@ant-design/icons/lib/icons/FolderOpenOutlined';
import ClockCircleOutlined from '@ant-design/icons/lib/icons/ClockCircleOutlined';

class MenuSidebar extends Component {
  render() {
    const {location} = this.props;
    const splitPathname = location.pathname.split('/').filter(i=>i);
    const selectedKeys = splitPathname.length === 0 ? [] : ['/' + splitPathname[0]];
    return (
      <Menu id="tk_Menu" defaultSelectedKeys={['/home']} mode="inline" selectedKeys={selectedKeys} theme="dark">
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
        <Menu.Item className="tk_MenuItem" key="/users">
          <Link to="/users">
            <UserOutlined/>
            <span>Users</span>
          </Link>
        </Menu.Item>
        <Menu.Item className="tk_MenuItem" key="/projects">
          <Link to="/projects">
            <FolderOpenOutlined />
            <span>Projects</span>
          </Link>
        </Menu.Item>
        <Menu.Item className="tk_MenuItem" key="/time_entries">
          <Link to="/time_entries">
            <ClockCircleOutlined />
            <span>Time entries</span>
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