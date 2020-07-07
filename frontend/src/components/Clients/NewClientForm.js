import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message} from 'antd';
import {Link, Redirect} from 'react-router-dom';
import {useTimeKeeperAPIPost} from '../../utils/services';
import Space from 'antd/lib/space';
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
    return (
      <React.Fragment>
        <Alert
          id="clientCreationError"
          message="Unable to save the new Client"
          description="Make sure that the Client name is unique"
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
          <Space className="tk_JcFe" size="middle" align="center">
            <Link id="btnCancelNewClient" className="tk_Btn tk_BtnSecondary" key="cancelLink" to={'/clients'}>Cancel</Link>
            <Button id="btnSubmitNewClient" className="tk_Btn tk_Btn tk_BtnPrimary" htmlType="submit">Submit</Button>
          </Space>
        </div>
      </Form>
    </React.Fragment>
  );

};

export default ClientForm;