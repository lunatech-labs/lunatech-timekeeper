import React, {useCallback, useEffect, useState} from 'react';
import {Button, Form, Input, message, PageHeader, Spin} from 'antd';
import {useAxios} from '../../utils/hooks';
import {Redirect, useRouteMatch} from 'react-router-dom';

const {TextArea} = Input;

const tailLayout = {
  wrapperCol: {
    offset: 4,
    span: 16,
  },
};

const EditClientForm = () => {

  const apiEndpoint = useAxios('http://localhost:8080');

  const [clientUpdated, setClientUpdated] = useState(false);

  const [loadedClient, setClient] = useState(null);

  const [clientId, setClientId] = useState(null);

  let clientIdSlug = useRouteMatch({
    path: '/clients/:id',
    strict: true,
    sensitive: true
  });

  useEffect(() => {
    // Extract the clientId from the path - EditClientForm does not need any other param
    let localClientId = clientIdSlug.params.id;
    setClientId(localClientId);
  }, [clientIdSlug]);

  useEffect(() => {
    if (apiEndpoint == null) {
      // When the page starts but Axios is not ready yet, we cannot fetch data
      // I guess there is a better pattern that we should use. Axios is async.
      return;
    }
    const fetchData = async () => {
      const result = await apiEndpoint.get('/api/clients/' + clientId);
      setClient(result.data);
    };
    fetchData();
  }, [apiEndpoint, clientId]);

  const postForm = useCallback(values => {
    apiEndpoint.put('/api/clients/' + clientId, {
      'name': values.name,
      'description': values.description
    })
      .then(res => {
        switch (res.status) {
        case 200:
          setClientUpdated(true);
          message.success('Client updated');
          break;
        case 204:
          setClientUpdated(true);
          message.success('Client updated');
          break;
        case 400:
          setClientUpdated(false);
          message.error('Invalid client, please check your form');
          break;
        case 401:
          setClientUpdated(false);
          message.error('Unauthorized : your role cannot edit this Client');
          break;
        case 500:
          setClientUpdated(false);
          message.error('Server error, something went wrong on the backend side');
          break;
        default:
          console.log('Invalid HTTP Code ');
          console.log(res.status);
          message.error('HTTP Code not handled ');
        }
      });
  }, [apiEndpoint, clientId]);

  if (clientUpdated) {
    return (
      <React.Fragment>
        <Redirect to="/clients"/>
      </React.Fragment>
    );
  }

  if (loadedClient) {
    return (
      <React.Fragment>
        <PageHeader title="Clients" subTitle="Edit a client"/>
        <Form
          labelCol={{span: 4}}
          wrapperCol={{span: 14}}
          layout="horizontal"
          onFinish={postForm}
          initialValues={loadedClient}
        >
          <Form.Item
            label="Name"
            name="name"
            rules={[
              {
                required: true,
              },
            ]}
          >
            <Input
              placeholder="Client's name"
            />
          </Form.Item>
          <Form.Item
            label="Description"
            name="description"
          >
            <TextArea
              rows={4}
              placeholder="A short description about this client"
            />
          </Form.Item>
          <Form.Item {...tailLayout}>
            <Button type="primary" htmlType="submit">
                            Submit
            </Button>
          </Form.Item>
        </Form>
      </React.Fragment>
    );
  }

  if (loadedClient == null) {
    return (
      <React.Fragment>
        <PageHeader title="Clients" subTitle="Edit a client"/>

        <Spin size="large">
          <Form
            labelCol={{span: 4}}
            wrapperCol={{span: 14}}
            layout="horizontal"
          >
            <Form.Item label="Name" name="name">
              <Input placeholder="Loading data from server..."/>
            </Form.Item>
            <Form.Item label="Description" name="description">
              <TextArea
                rows={4}
                placeholder="Loading data from server..."
              />
            </Form.Item>
          </Form>
        </Spin>

      </React.Fragment>
    );
  }
};

export default EditClientForm;