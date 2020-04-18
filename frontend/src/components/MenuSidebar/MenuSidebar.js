import React, {Component} from 'react';
import {Link, withRouter} from "react-router-dom";
import {Menu} from "antd";
import './MenuSidebar.less';
import PropTypes from "prop-types";

import {DesktopOutlined, PieChartOutlined} from "@ant-design/icons";

class MenuSidebar extends Component {
    static propTypes = {
        location: PropTypes.object.isRequired,
    };

    render() {
        const {location} = this.props;
        return (
            <Menu defaultSelectedKeys={['/home']} mode="inline" selectedKeys={[location.pathname]}>
                <Menu.Item key="/home">
                    <PieChartOutlined />
                    <span><Link to="/home">Home</Link></span>
                </Menu.Item>
                <Menu.Item key="/clients">
                    <DesktopOutlined/>
                    <span><Link to="/clients">Clients</Link></span>
                </Menu.Item>
            </Menu>
        )
    }
}

export default withRouter(MenuSidebar);