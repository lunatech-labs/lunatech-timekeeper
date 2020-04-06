import React from 'react'
import Keycloak from 'keycloak-js'
import { KeycloakProvider } from '@react-keycloak/web'
import logo from './logo_timekeeper_homepage.png';
import './App.css';
import { AppRouter } from './routes'

const keycloak = new Keycloak({
    // Il est possible de configurer via des variables d'environement pour la PROD
    // realm: process.env.REACT_APP_KEYCLOAK_REALM,
    //url: process.env.REACT_APP_KEYCLOAK_URL,
    //clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID,
    realm: "quarkus",
    url: "http://localhost:8082/auth/",
    clientId: "react-ff",
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
                    <header className="App-header">
                        <div>
                            <img src={logo} className="App-logo" alt="logo" />
                        </div>
                        <div>
                            <AppRouter />
                        </div>
                    </header>
                </div>


            </KeycloakProvider>
        )
    }
}

export default App
