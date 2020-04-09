import React from 'react'
import Keycloak from 'keycloak-js'
import TempHeader from './components/atoms/TempHeader';
import logo from './logo_timekeeper_homepage.png';

import { KeycloakProvider } from '@react-keycloak/web'
import { AppRouter } from './routes'

import './App.css';

const keycloak = new Keycloak({
    // Il est possible de configurer via des variables d'environement pour la PROD
    // realm: process.env.REACT_APP_KEYCLOAK_REALM,
    //url: process.env.REACT_APP_KEYCLOAK_URL,
    //clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID,
    realm: "Timekeeper",
    url: "http://localhost:8082/auth/",
    clientId: "react-timekeeper-client",
    publicClient: "true"
})

const keycloakProviderInitConfig = {
    onLoad: 'check-sso',
}

class App extends React.PureComponent {
    onKeycloakEvent = (event, error) => {
        console.log('onKeycloakEvent', event, error)
    }

    onKeycloakTokens = (tokens) => {
        console.log('onKeycloakTokens', tokens)
    }

    render() {
        return (
            <KeycloakProvider
                keycloak={keycloak}
                initConfig={keycloakProviderInitConfig}
                onEvent={this.onKeycloakEvent}
                onTokens={this.onKeycloakTokens}
            >

                <div className="App">
                    <TempHeader></TempHeader>
                    <header className="App-header">
                    </header>
                    <body className="App-body">
                        <AppRouter />
                    </body>
                </div>


            </KeycloakProvider>
        )
    }
}

export default App
