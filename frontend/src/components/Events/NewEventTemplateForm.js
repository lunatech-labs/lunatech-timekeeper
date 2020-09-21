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
import {sortListByName, useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import {Alert, Button, Col, Form, Input, message, Row, Space, Spin, DatePicker, Radio, Select} from 'antd';
import './NewEventTemplateForm.less';
import '../../components/Button/BtnGeneral.less';
import {Link, Redirect} from 'react-router-dom';
import TitleSection from '../../components/Title/TitleSection';
import moment from 'moment';
import UserTreeData from './UserTreeData';
import _ from 'lodash';
import 'moment/locale/en-gb';

const {TextArea} = Input;
const { RangePicker } = DatePicker;
const {Option} = Select;

const USER_EVENTS = ['Vacations', 'Sickness', 'Maternity/Paternity leave', 'Family event'];

const NewEventTemplateForm = () => {
  const [eventTemplateCreated, setEventTemplateCreated] = useState(false);
  const [usersSelected, setUsersSelected] = useState([]);

  const formDataToEventRequest = (formData) => ({
    name: formData.name,
    description: formData.description,
    startDateTime: formData.eventDateTime[0],
    endDateTime: formData.eventDateTime[1],
    attendees: usersSelected,
    // eventType: formData.eventType : Shouldn't be sent while the backend is not implemented
    // eventName: formData.eventName : Shouldn't be sent while the backend is not implemented
  });
  const usersResponse = useTimeKeeperAPI('/api/users');
  const eventsResponse = useTimeKeeperAPI('/api/events');
  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/events', (form => form) , setEventTemplateCreated, formDataToEventRequest);

  const [form] = Form.useForm();

  const initialValues = {
    name: '',
    description: '',
    eventDateTime: [moment.utc('9:00 AM', 'LT'), moment.utc('9:00 AM', 'LT')],
    attendees: [],
    eventType: 'COMPANY_EVENT',
    eventName: ''
  };

  useEffect(() => {
    if (!eventTemplateCreated) {
      return;
    }
    message.success('Event was created');
  }, [eventTemplateCreated]);

  if (eventTemplateCreated) {
    return (
      <React.Fragment>
        <Redirect to="/events"/>
      </React.Fragment>
    );
  }

  const disabledDate = (current) => {
    // Can not select days before today and today
    return current && current < moment().subtract(1, 'days').endOf('day');
  };

  function disabledTime(time, type) {
    if (type === 'start') {
      return {
        disabledHours() {
          let morning =  _.range(0, 7);
          let evening =  _.range(20, 24);
          return _.concat(morning,evening);
        },
        disabledMinutes: function () {
          return _.filter(_.range(0, 60), function (x) {
            return x % 15 !== 0;
          });
        },
        disabledSeconds() {
          return _.range(0, 60);
        },
      };
    }
    return {
      disabledHours() {
        let morning =  _.range(0, 7);
        let evening =  _.range(20, 24);
        return _.concat(morning,evening);
      },
      disabledMinutes: function () {
        return _.filter(_.range(0, 60), function (x) {
          return x % 15 !== 0;
        });
      },
      disabledSeconds() {
        return _.range(0, 60);
      },
    };
  }

  if(eventsResponse.data && usersResponse.data){
    return (
        <Form
            id="tk_Form"
            className="addEvent"
            layout="vertical"
            initialValues={initialValues}
            onFinish={timeKeeperAPIPost.run}
            form={form}
        >
          {timeKeeperAPIPost.error &&
          <Alert
              message="Unable to save the new Event"
              description={timeKeeperAPIPost.error.data.message}
              type="error"
              closable
              style={{marginBottom: 10}}
          />}
          <div className="tk_CardLg">
            <Row gutter={16}>
              <Col className="gutter-row" span={12}>
                <TitleSection title="Information"/>

                    <Form.Item label="Event type:" name="eventType" rules={[{required: true}]}>
                      <Radio.Group>
                        <Radio.Button value="COMPANY_EVENT">Company event</Radio.Button>
                        <Radio.Button value="USER_EVENT">User event</Radio.Button>
                      </Radio.Group>
                    </Form.Item>

                    <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.eventType !== curValues.eventType || prevValues.eventName !== curValues.eventName}>
                      {({getFieldValue}) => {
                        switch (getFieldValue('eventType')) {
                          case 'USER_EVENT':
                            return (
                                <Form.Item
                                    name="eventName"
                                    label="Events"
                                    rules={[{required: true}]}
                                >
                                  <Select>{USER_EVENTS.map(i =>
                                      <Option key={`option-event-${i}`} value={i}>{i}</Option>)}
                                  </Select>
                                </Form.Item>
                            );
                          case 'COMPANY_EVENT':
                            return (
                                <Form.Item
                                    name="eventName"
                                    label="Events"
                                    rules={[{required: true}]}
                                    >
                                  <Input placeholder="Please enter event's name" type="text"/>
                                </Form.Item>
                            );
                          default:
                            return (<div></div>);
                        }
                      }}
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
                      disabledTime={disabledTime}
                      showTime={{
                        hideDisabledOptions: true
                      }}
                      format="DD-MM-YYYY HH:mm"
                      className="tk_RangePicker"
                  />
                </Form.Item>
              </Col>

              <Col className="gutter-row" span={12}>
                <TitleSection title="Users"/>
                <Form.Item
                    label="Select users :"
                    name="usersSelected"
                >
                  <UserTreeData users={sortListByName(usersResponse.data)} usersSelected={usersSelected} setUsersSelected={setUsersSelected}/>
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

  if (eventsResponse.loading || usersResponse.loading) {
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

  if (eventsResponse.error || usersResponse.error) {
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

export default NewEventTemplateForm;