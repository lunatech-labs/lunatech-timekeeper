import React, { Component } from 'react';
import logo from '../../img/logo_TK_x2_retina.png';
import './Logo.less';

class Logo extends Component {
  render() {
    return (
      <div className="sider-logo">
        <a href="home" className="">
          <img src={logo} alt="logo"/>
          <h1>timekeeper</h1>
        </a>
      </div>
    );
  }
}

export default Logo;