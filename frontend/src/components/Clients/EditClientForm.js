import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message, Spin,} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import {CheckOutlined, CloseOutlined} from '@ant-design/icons';

const {TextArea} = Input;

const EditClientForm = () => {

  const [clientUpdated, setClientUpdated] = useState(false);

  const clientIdSlug = useRouteMatch({
    path: '/clients/:id',
    strict: true,
    sensitive: true
  });

  const clientResponse = useTimeKeeperAPI('/api/clients/' + clientIdSlug.params.id);

  const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/clients/' + clientIdSlug.params.id, (form=>form), setClientUpdated);

  useEffect(() => {
    if(!clientUpdated) {
      return;
    }
    message.success('Client was updated');
  }, [clientUpdated]);

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
        <Form
          id="tk_Form"
          layout="vertical"
          initialValues={clientResponse.data}
          onFinish={timeKeeperAPIPut.run}
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