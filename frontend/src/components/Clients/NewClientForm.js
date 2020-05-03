import React, {useState} from 'react';
import {Button, Form,  Input, message, Alert, PageHeader} from 'antd';
import {Redirect, Link} from 'react-router-dom';
import {useTimeKeeperAPIPost} from '../../utils/services';

const {TextArea} = Input;

const tailLayout = {
  wrapperCol: {
    offset: 4,
    span: 16,
  },
};

const ClientForm = () => {

  const [clientCreated, setClientCreated] = useState(false);

  const timeKeeperAPIPost = useTimeKeeperAPIPost(   '/api/clients', (form => form), setClientCreated);

  if (clientCreated) {
    message.success('Client was created');
    return (
      <React.Fragment>
        <Redirect to="/clients"/>
      </React.Fragment>
    );
  }

  if (timeKeeperAPIPost.error) {
    const { response } = timeKeeperAPIPost.error;
    const { status, url } = response;
    const  errMsg  =  `Server error HTTP Code:${status}  for url: ${url}`;
    return (
      <React.Fragment>
        <Alert
          message="Unable to save the new Client"
          description={errMsg}
          type="error"
          closable
          style={{marginBottom: 10}}
        />
      </React.Fragment>
    );
  }

  return (
    <React.Fragment>
      <Form
        labelCol={{span: 4}}
        wrapperCol={{span: 14}}
        layout="horizontal"
        onFinish={timeKeeperAPIPost.run}
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

};

export default ClientForm;