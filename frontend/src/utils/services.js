import {useKeycloak} from '@react-keycloak/web';
import {useEffect} from 'react';
import {useRequest} from '@umijs/hooks';
// This must imported else the Request will not use the Bearer
// eslint-disable-line no-unused-vars
import request from './request';
import {notification} from 'antd';

const codeMessage = {
  200: 'Ok',
  201: 'Created',
  204: 'No Content',
  400: 'Bad Request',
  401: 'Unauthorized',
  403: 'Forbidden',
  404: 'Resource not found',
  500: 'Server Error',
};

function errorHandler(error) {
  const {response} = error;

  if (response && response.status) {
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
      const promiseResult = response.json();

      promiseResult.then(function (bodyTxt) {
        console.log(bodyTxt.message);
        const errorText = 'Message from server: ' + bodyTxt.message;
        const {status, url} = response;

        notification.error({
          message: `Server error ${status}: ${url}`,
          description: errorText,
        });
      });
    } else {
      return response.text().then(text => {
        const errorText = codeMessage[response.status] || response.statusText + ' ' + text;
        const {status, url} = response;
        notification.error({
          message: `Server error ${status}: ${url}`,
          description: errorText,
        });
      });

    }
  }

  return response;
}

export const useTimeKeeperAPI = (urlAPI, ...rest) => {
  const [keycloak, initialized] = useKeycloak();

  // Do a dummy usage of request to avoid a warn at compile
  // request must be imported to override the useRequest default `request`
  // NMA
  if (request.name == null) {
    console.log('Error: request should be set to umiInstance');
  }

  useEffect(() => {
    if (initialized) {
      localStorage.setItem('x-auth-token', keycloak.token);
    }
  }, [urlAPI, initialized, keycloak]
  );

  const toReturn = useRequest(process.env.REACT_APP_QUARKUS_BACKEND + urlAPI, ...rest);

  if (toReturn.error) {
    console.log('got an error');
    errorHandler(toReturn.error);
  }

  return toReturn;

};

/**
 * A Callback method to POST a form to the server.
 *
 * @param urlAPI the endpoint to call
 * @param formData : form data
 * @param booleanCallback : a function that accepts a boolean, to indicate if it was successful
 */
export const useTimeKeeperAPIPost = (urlAPI, formData, booleanCallback) => {
  const [keycloak, initialized] = useKeycloak();

  useEffect(() => {
    if (initialized) {
      localStorage.setItem('x-auth-token', keycloak.token);
    }
  }, [urlAPI, initialized, keycloak]
  );

  return useRequest((formData) => {
    return ({
      url: process.env.REACT_APP_QUARKUS_BACKEND + urlAPI,
      method: 'post',
      data: formData
    });
  }, {
    manual: true,
    onSuccess: () => {
      if (booleanCallback) {
        booleanCallback(true);
      } else {
        console.error('Err: please set a callback for POST call to ' + urlAPI);
      }
    }
  });
};

export const useTimeKeeperAPIPut = (urlAPI, formData, booleanCallback) => {
  const [keycloak, initialized] = useKeycloak();

  useEffect(() => {
    if (initialized) {
      localStorage.setItem('x-auth-token', keycloak.token);
    }
  }, [urlAPI, initialized, keycloak]
  );

  return useRequest((formData) => ({
    url: process.env.REACT_APP_QUARKUS_BACKEND + urlAPI,
    method: 'put',
    data: formData
  }), {
    manual: true,
    onSuccess: () => {
      if (booleanCallback) {
        booleanCallback(true);
      } else {
        console.error('Err: please set a callback for PUT call to ' + urlAPI);
      }
    }
  });
};