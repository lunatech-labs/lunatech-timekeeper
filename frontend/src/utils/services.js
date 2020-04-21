import {useKeycloak} from '@react-keycloak/web';
import {useEffect} from 'react';
import {useRequest} from '@umijs/hooks';
// This must imported else the Request will not use the Bearer
// eslint-disable-line no-unused-vars
import request from './request';

export const useTimeKeeperAPI = (urlAPI, ...rest) => {
  const [keycloak, initialized] = useKeycloak();

  // Do a dummy usage of request to avoid a warn at compile
  // request must be imported to override the useRequest default `request`
  // NMA
  if(request.name == null){
    console.log('Error: request should be set to umiInstance');
  }

  useEffect(() => {
    if (initialized) {
      localStorage.setItem('x-auth-token', keycloak.token);
    }
  }, [urlAPI, initialized, keycloak]
  );

  return useRequest('http://localhost:8080' + urlAPI, ...rest);
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

  return useRequest((formData) => ({
    url: 'http://localhost:8080' + urlAPI,
    method: 'post',
    data: formData
  }), {
    manual:true,
    onSuccess: () => {
      if(booleanCallback) {
        booleanCallback(true);
      }else{
        console.log('Err: please set a callback for POST call to '+urlAPI);
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
    url: 'http://localhost:8080' + urlAPI,
    method: 'put',
    data: formData
  }), {
    manual:true,
    onSuccess: () => {
      if(booleanCallback) {
        booleanCallback(true);
      }else{
        console.log('Err: please set a callback for PUT call to '+urlAPI);
      }
    }
  });
};