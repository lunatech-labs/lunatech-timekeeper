import React, {useState} from 'react';
import {
  Alert,
  Button,
  Form,
  Input,
  PageHeader,
  Spin,
} from 'antd';
import {Redirect, useRouteMatch, Link} from 'react-router-dom';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';

const {TextArea} = Input;

const tailLayout = {
  wrapperCol: {
    offset: 4,
    span: 16,
  },
};

const EditClientForm = () => {

  const [clientUpdated, setClientUpdated] = useState(false);

  const clientIdSlug = useRouteMatch({
    path: '/clients/:id',
    strict: true,
    sensitive: true
  });

  const clientResponse = useTimeKeeperAPI('/api/clients/' + clientIdSlug.params.id);

  const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/clients/' + clientIdSlug.params.id, (form=>form), setClientUpdated);

  if (clientUpdated) {
    return (
      <React.Fragment>
        <Redirect to="/clients"/>
      </React.Fragment>
    );
  }

  if (clientResponse.data) {
    return (
      <React.Fragment>
        <PageHeader title="Clients" subTitle="Edit a client"/>
        <Form
          labelCol={{span: 4}}
          wrapperCol={{span: 14}}
          layout="horizontal"
          initialValues={clientResponse.data}
          onFinish={timeKeeperAPIPut.run}
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
            <Link key="cancelLink" to={'/clients'}>
              <Button htmlType="button">
                Cancel
              </Button>
            </Link>
            <Button type="primary" htmlType="submit">
                            Submit
            </Button>
          </Form.Item>
        </Form>
      </React.Fragment>
    );
  }

  if (clientResponse.loading) {
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

  if(clientResponse.error){
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the client'
          type='error'
        />
      </React.Fragment>
    );
  }

  if(timeKeeperAPIPut.error){
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to save the edited client'
          type='error'
        />
      </React.Fragment>
    );
  }

};

export default EditClientForm;