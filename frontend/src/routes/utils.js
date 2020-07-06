import React from 'react';
import { Route, Redirect } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import PropTypes from 'prop-types';
import {message} from 'antd';

export function PrivateRoute({ component: Component, roles, specialRights, ...rest }) {
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

  if (keycloak.authenticated){
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

    {/*<Route
      {...rest}
      render={(props) =>
        <Redirect
          to={{
            pathname: '/login',
            state: { from: props.location },
          }}
        />
      }
    />*/}
  return (
    <ForbiddenRoute {...rest}/>
  );

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
  specialRights: PropTypes.func
};