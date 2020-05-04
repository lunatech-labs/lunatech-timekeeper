import React, {useState} from 'react';
import {Alert, Button, Form, Input, message, Spin,} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';

const {TextArea} = Input;

const tailLayout = {
  wrapperCol: {
    offset: 4,
    span: 14,
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
    message.success('Client was updated');
    return (
      <React.Fragment>
        <Redirect to="/clients"/>
      </React.Fragment>
    );
  }

  if (clientResponse.data) {
    return (
      <React.Fragment>
        <div style={{ borderTop: '1px solid rgba(216, 216, 216, 0.1)', marginTop: 48 }}>&nbsp;</div>
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
            <Space size="middle" style={{right: 0, position: 'absolute'}}>
              <Link key="cancelLink" to={'/clients'}>
                <Button htmlType="button">
                  Cancel
                </Button>
              </Link>
              <Button type="primary" htmlType="submit">
                Submit
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </React.Fragment>
    );
  }

  if (clientResponse.loading) {
    return (
      <React.Fragment>
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