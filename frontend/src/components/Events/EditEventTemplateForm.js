/*
 * Copyright 2020 Lunatech S.A.S
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
import {Alert, Button, Col, DatePicker, Form, Input, message, Row, Spin,} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {sortListByName, useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';
import moment from 'moment';
import TitleSection from '../Title/TitleSection';
import UserTreeData from './UserTreeData';
import {range} from '../../utils/utils';

const {TextArea} = Input;
const { RangePicker } = DatePicker;

const EditEventTemplateForm = () => {

  const [eventTemplateUpdated, setEventTemplateUpdated] = useState(false);

  const formDataToEventRequest = (formData) => ({
    name: formData.name,
    description: formData.description,
    startDateTime: formData.eventDateTime[0],
    endDateTime: formData.eventDateTime[1],
    attendees: []
  });

  const eventIdSlug = useRouteMatch({
    path: '/events/:id',
    strict: true,
    sensitive: true
  });

  const eventsResponse = useTimeKeeperAPI('/api/events/');
  const usersResponse = useTimeKeeperAPI('/api/users/');

  const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/events/' + eventIdSlug.params.id, (form=>form), setEventTemplateUpdated, formDataToEventRequest);

  const [form] = Form.useForm();

  useEffect(() => {
    if (!eventTemplateUpdated) {
      return;
    }
    message.success('Event was updated');
  }, [eventTemplateUpdated]);

  if (eventTemplateUpdated) {
    return (
      <React.Fragment>
        <Redirect to="/events"/>
      </React.Fragment>
    );
  }

  if (timeKeeperAPIPut.error) {
    const {response} = timeKeeperAPIPut.error;
    const {status, url} = response;
    const errMsg = `Server error HTTP Code:${status}  for url: ${url}`;
    return (
      <React.Fragment>
        <Alert
          message="Unable to update the new Event"
          description={errMsg}
          type="error"
          closable
          style={{marginBottom: 10}}
        />
      </React.Fragment>
    );
  }

  const disabledDate = (current) => {
    // Can not select days before today and today
    return current && current < moment().endOf('day');
  };

  const disabledRangeTime = (_, type) => {
    if (type === 'start') {
      return {
        disabledMinutes: () => range(0, 59).filter(minute => minute % 15 !== 0)
      };
    }
    return {
      disabledMinutes: () => range(0, 59).filter(minute => minute % 15 !== 0)
    };
  };

  if(usersResponse.data && eventsResponse.data){
    const event = eventsResponse.data.find(event => event.id.toString() === eventIdSlug.params.id);
    if(event){
      const initialValues = {
        name: event.name,
        description: event.description,
        eventDateTime: [moment.utc(event.startDateTime),moment.utc(event.endDateTime)],
        attendees: []
      };
      return (
        <Form
          id="tk_Form"
          layout="vertical"
          initialValues={initialValues}
          onFinish={timeKeeperAPIPut.run}
          form={form}
        >
          <div className="tk_CardLg">
            <Row gutter={16}>
              <Col className="gutter-row" span={12}>
                <TitleSection title="Information"/>
                <Form.Item
                  label="Name :"
                  name="name"
                  hasFeedback
                  rules={[
                    {
                      required: true,
                    },
                  ]}
                >
                  <Input
                    placeholder="Event's name"
                  />
                </Form.Item>
                <Form.Item
                  label="Description :"
                  name="description"
                >
                  <TextArea
                    rows={4}
                    placeholder="A short description about this event"
                  />
                </Form.Item>

                <Form.Item
                  label="Date and duration"
                  name="eventDateTime"
                  rules={[
                    {
                      required: true,
                    },
                  ]}
                >
                  <RangePicker
                    disabledDate={disabledDate}
                    disabledTime={disabledRangeTime}
                    showTime={{
                      hideDisabledOptions: true
                    }}
                    format="YYYY-MM-DD h:mm a"
                    className="tk_RangePicker"
                  />
                </Form.Item>
              </Col>
              <Col className="gutter-row" span={12}>
                <TitleSection title="Users"/>
                <Form.Item
                  label="Select users :"
                >
                  <UserTreeData users={sortListByName(usersResponse.data)}/>
                </Form.Item>
              </Col>
            </Row>
            <Space className="tk_JcFe" size="middle" align="center">
              <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/events'}>Cancel</Link>
              <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Submit</Button>
            </Space>
          </div>
        </Form>
      );
    }
  }

  if (usersResponse.loading || eventsResponse.loading) {
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

  if (usersResponse.error || eventsResponse.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the data'
          type='error'
        />
      </React.Fragment>
    );
  }
};
export default EditEventTemplateForm;