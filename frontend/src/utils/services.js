import {useKeycloak} from '@react-keycloak/web';
import {useEffect} from "react";
import {useRequest} from "@umijs/hooks";
// This must imported else the Request will not use the Bearer
// eslint-disable-line no-unused-vars
import request from "./request";

export const useTimeKeeperAPI = (urlAPI, ...rest) => {
    const [keycloak, initialized] = useKeycloak();

    // Do a dummy usage of request to avoid a warn at compile
    // request must be imported to override the useRequest default `request`
    // NMA
    if(request.name == null){
        console.log("Error: request should be set to umiInstance");
    }

    useEffect(() => {
            if (initialized) {
                localStorage.setItem('x-auth-token', keycloak.token);
            }
        }, [urlAPI, initialized, keycloak]
    );

    const {data, error, loading} = useRequest('http://localhost:8080' + urlAPI, ...rest);

    return {data, error, loading}
};

