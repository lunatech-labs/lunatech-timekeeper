import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message} from 'antd';
import {Link, Redirect} from 'react-router-dom';
import {useTimeKeeperAPIPost} from '../../utils/services';
import Space from 'antd/lib/space';
import {CheckOutlined, CloseOutlined} from '@ant-design/icons';
import '../../components/Button/BtnGeneral.less';

const {TextArea} = Input;

const initialValues = {
  name: '',
  description: ''
};

const ClientForm = () => {

  const [clientCreated, setClientCreated] = useState(false);

  const timeKeeperAPIPost = useTimeKeeperAPIPost(   '/api/clients', (form => form), setClientCreated);

  useEffect(() => {
    if(!clientCreated) {
      return;
    }
    message.success('Client was created');
  }, [clientCreated]);

  if (clientCreated) {
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
        id="tk_Form"
        layout="vertical"
        onFinish={timeKeeperAPIPost.run}
        initialValues={initialValues}
      >
        <div className="tk_CardLg">
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
        </div>
        <Form.Item>
          <Space className="tk_JcFe" size="middle" align="center">
            <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/clients'}><CloseOutlined />Cancel</Link>
            <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit"><CheckOutlined />Submit</Button>
          </Space>
        </Form.Item>
      </Form>
    </React.Fragment>
  );

};

export default ClientForm;