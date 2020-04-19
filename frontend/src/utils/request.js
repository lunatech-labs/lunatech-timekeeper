import { extend } from 'umi-request';
import { notification } from 'antd';

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

const errorHandler = error => {
    const { response } = error;

    if (response && response.status) {
        const errorText = codeMessage[response.status] || response.statusText;
        const { status, url } = response;
        notification.error({
            message: `Server error ${status}: ${url}`,
            description: errorText,
        });
    }

    return response;
};
const request = extend({
    errorHandler,
    credentials: 'same-origin',
});

request.interceptors.request.use(async (url, options) => {
    let c_token = localStorage.getItem("x-auth-token");
    if (c_token) {
        const headers = {
            'X-Client':'umi request for timekeeper',
            'Authorization': `Bearer ${c_token}`
        };
        return (
            {
                url: url,
                options: { ...options, headers: headers },
            }
        );
    } else {
        const headers = {
            'X-Client': 'not_authenticated',
        };
        return (
            {
                url: url,
                options: { ...options , headers: headers },
            }
        );
    }

});


export default request;
