/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
          <Space className="tk_JcFe" size="middle" align="center">
            <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/clients'}>Cancel</Link>
            <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Submit</Button>
          </Space>
        </div>
      </Form>
    </React.Fragment>
  );

};

export default ClientForm;