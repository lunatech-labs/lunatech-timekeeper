import React, { useCallback } from 'react'
import { Redirect, withRouter } from 'react-router-dom'
import { withKeycloak } from '@react-keycloak/web'
import { Button } from "antd";
import { Layout } from 'antd';
const {  Footer, Content } = Layout;

const LoginPage = withRouter(
    withKeycloak(({ keycloak, location }) => {
        const { from } = location.state || { from: { pathname: '/home' } }
        if (keycloak.authenticated) return <Redirect to={from} />

        const login = useCallback(() => {
            keycloak.login()
        }, [keycloak])

        return (
            <Layout>
                <Content>
                    <Button type="primary" onClick={login}>
                        Login
                    </Button>
                </Content>
                <Footer>Copyright(c) 2020 - Lunatech</Footer>
            </Layout>
        )
    })
)

export default LoginPage
