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
import {Alert, Button, Form, Input, message, Spin,} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';

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
              <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/clients'}>Cancel</Link>
              <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Submit</Button>
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