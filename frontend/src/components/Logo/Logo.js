import React, { Component } from 'react';
import logo from '../../img/logo_TK_x2_retina.png'
import './Logo.less';

class Logo extends Component {
    render() {
        return (
            <a href="home" className="sider-logo">
                <img src={logo} alt="logo"/>
                timekeeper
            </a>
        )
    }
}

export default Logo;