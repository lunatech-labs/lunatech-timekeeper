import React, {useCallback} from 'react';
import {Redirect, withRouter} from 'react-router-dom';
import {withKeycloak} from '@react-keycloak/web';
import {Button, Layout, Row, Col, Typography} from 'antd';

import Logo from '../../img/logo.png';
import LoginBackground from '../../img/login_Background.png';

import './Login.less';

const {Content} = Layout;
const {Title, Paragraph} = Typography;

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
            <Col className="login_LeftPart" span={9}>
              <div className="logo_Tk">
                <img src={Logo} alt=""/>
              </div>
              <div className="title_Tk">
                <Title>Simple time tracking. Powerful reporting.</Title>
                <Paragraph>Turn your team on to productivity.</Paragraph>
              </div>

              <Button type="primary" onClick={login} danger>
                                    Login
              </Button>

            </Col>
            <Col className="login_RightPart" span={15}>
              <img src={LoginBackground} alt=""/>
            </Col>
          </Row>
        </Content>
      </Layout>
    );
  })
);

export default LoginPage;
