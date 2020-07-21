/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useCallback} from 'react';
import {Redirect, withRouter} from 'react-router-dom';
import {withKeycloak} from '@react-keycloak/web';
import {Button, Layout, Row, Col, Typography} from 'antd';

import Logo from '../../img/logo_TK_homepage_x2_retina.png';
import LoginBackground from '../../img/login_Background.png';

import './Login.less';

const {Content} = Layout;
const {Title} = Typography;

const LoginPage = withRouter(
  withKeycloak(({keycloak, location}) => {
    const {from} = location.state || {from: {pathname: '/home'}};
    if (keycloak.authenticated) return <Redirect to={from}/>;

    const login = useCallback(() => {
      keycloak.login();
    }, [keycloak]);

    return (
      <Layout>
        <Content>
          <Row>
            <Col id="login_LeftPart" span={10}>
              <div className="logo_Tk">
                <img src={Logo} alt=""/>
                <p>timekeeper</p>
              </div>
              <div id="title_Tk">
                <Title>Simple time tracking. Powerful reporting.</Title>
                <p>Turn your team on to productivity.</p>
                <Button id="btnLogin" className="tk_Btn tk_BtnPrimary" onClick={login}>Sign up</Button>
              </div>
              <div className="bottom_Tk">
              </div>
            </Col>
            <Col id="login_RightPart" span={14}>
              <img src={LoginBackground} alt=""/>
            </Col>
          </Row>
        </Content>
      </Layout>
    );
  })
);

export default LoginPage;
