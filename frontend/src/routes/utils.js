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

import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import PropTypes from 'prop-types';
import {message} from 'antd';

export function PrivateRoute({ component: Component, roles, ...rest }) {
  const [keycloak] = useKeycloak();

  const roleChecked = (roles) => (roles === undefined || roles.some(role => keycloak.hasRealmRole(role)));

  if (keycloak.authenticated && roleChecked(roles)) {
    return (
      <Route
        {...rest}
        render={(props) =>
          <Component {...props} />
        }
      />
    );
  }

  if (keycloak.authenticated) {
    return <ForbiddenRoute {...rest} />;
  }

  return <Route
    {...rest}
    render={() =>
      <Redirect
        to={{
          pathname: '/login',
        }}
      />
    }
  />;

}

export function ForbiddenRoute({ ...rest }) {
  message.error('Access forbidden for this page');
  return <Route
    {...rest}
    render={() =>
      <Redirect
        to={{
          pathname: '/',
        }}
      />
    }
  />;
}


PrivateRoute.propTypes ={
  component: PropTypes.func.isRequired,
  roles: PropTypes.arrayOf(PropTypes.string),
  location: PropTypes.object,
};