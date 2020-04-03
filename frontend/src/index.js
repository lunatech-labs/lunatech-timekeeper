import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import * as Keycloak from 'keycloak-js'

let initOptions = {
    url: 'http://127.0.0.1:8082/auth',
    realm: 'quarkus',
    clientId: 'react-ff',
    onLoad: 'login-required',
    promiseType: 'native',
};

// Source : https://github.com/akoserwal/keycloak-integrations/blob/master/quarkus-react-keycloak/react-ff/src/index.js
const keycloak = Keycloak(initOptions);
keycloak.init({ onLoad: initOptions.onLoad }).success((auth) => {

    if (!auth) {
        window.location.reload();
    } else {
        console.info("Authenticated");
    }

    //React Render
    ReactDOM.render(
        <React.StrictMode>
            <App />
        </React.StrictMode>,
        document.getElementById('root')
    );

    localStorage.setItem("react-token", keycloak.token);
    localStorage.setItem("react-refresh-token", keycloak.refreshToken);

    setTimeout(() => {
        keycloak.updateToken(70).success((refreshed) => {
            if (refreshed) {
                console.debug('Token refreshed' + refreshed);
            } else {
                console.warn('Token not refreshed, valid for '
                    + Math.round(keycloak.tokenParsed.exp + keycloak.timeSkew - new Date().getTime() / 1000) + ' seconds');
            }
        }).error(() => {
            console.error('Failed to refresh token');
        });


    }, 60000)
}).error(() => {
    console.error("Authenticated Failed");
});


// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
