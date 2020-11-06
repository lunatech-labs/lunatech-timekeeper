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

import React, {useState} from 'react';
import Keycloak from 'keycloak-js';
import { KeycloakProvider } from '@react-keycloak/web';
import { AppRouter } from './routes';
import { ContextTheme} from './components/Theme/ContextTheme';
import './App.less';

const keycloak = new Keycloak({
  realm: process.env.REACT_APP_KEYCLOAK_REALM,
  url: process.env.REACT_APP_KEYCLOAK_URL,
  clientId: process.env.REACT_APP_KEYCLOAK_CLIENT_ID
});

const keycloakProviderInitConfig = {
  onLoad: 'check-sso',
};

function TestPago ({children}) {
  const [theme, setTheme] = useState('dark-theme');

  const changeTheme = {
    theme, toggleTheme: () => {
      if (theme === 'light-theme') {
        setTheme('dark-theme');
      } else {
        setTheme('light-theme');
      }
    },
  }

  return (
    <ContextTheme.Provider value={changeTheme}>
      <div className={theme}>
        {children}
      </div>
    </ContextTheme.Provider>
  )
}

class App extends React.PureComponent {
  render() {
    return (
      <KeycloakProvider
        keycloak={keycloak}
        initConfig={keycloakProviderInitConfig}
        onEvent={this.onKeycloakEvent}
        onTokens={this.onKeycloakTokens}
      >
       <TestPago>
          <AppRouter />
        </TestPago> 
      </KeycloakProvider>
    );
  }
}

export default App;
