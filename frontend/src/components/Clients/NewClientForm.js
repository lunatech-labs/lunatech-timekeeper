import React, {useState} from 'react';
import {Alert, Button, Form, Input, message} from 'antd';
import {Link, Redirect} from 'react-router-dom';
import {useTimeKeeperAPIPost} from '../../utils/services';
import Space from 'antd/es/space';

const {TextArea} = Input;

const tailLayout = {
  wrapperCol: {
    offset: 4,
    span: 14,
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
      <div style={{ borderTop: '1px solid rgba(216, 216, 216, 0.1)', marginTop: 48 }}>&nbsp;</div>
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

};

export default ClientForm;