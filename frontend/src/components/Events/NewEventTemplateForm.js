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
const {Option} = Select;

const NewEventTemplateForm = () => {
  const [eventTemplateCreated, setEventTemplateCreated] = useState(false);
  const [usersSelected, setUsersSelected] = useState([]);

  const formDataToEventRequest = (formData) => ({
    name: formData.name,
    description: formData.description,
    startDateTime: moment.utc(formData.startDateTime).add(formData.startDateNumberOfHours, 'hours'),
    endDateTime: moment.utc(formData.endDateTime).add(formData.endDateNumberOfHours, 'hours'),
    attendees: usersSelected,
    // eventType: formData.eventType : Shouldn't be sent while the backend is not implemented
  });
  const usersResponse = useTimeKeeperAPI('/api/users');
  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/events', (form => form) , setEventTemplateCreated, formDataToEventRequest);

  const [form] = Form.useForm();

  const initialValues = {
    name: '',
    description: '',
    timeUnitStartDate:'DAY',
    startDateTime: moment.utc('9:00 AM', 'LT') ,
    startDateNumberOfHours: 1,
    timeUnitEndDate:'DAY',
    endDateTime: moment.utc('09:00 AM', 'LT'),
    endDateNumberOfHours: 1,
    attendees: [],
    eventType: 'COMPANY_EVENT'
  };

  const timeUnitToNumberOfHour = (timeUnit) => {
    switch (timeUnit) {
      case 'DAY': {
        return 8;
      }
      case 'HALFDAY': {
        return 4;
      }
      case 'HOURLY' : {
        return 1;
      }
      default: {
        return {};
      }
    }
  };

  // Update the values when a field changes
  const onValuesChange = (changedValues, allValues) => {
    const key = Object.keys(changedValues)[0];
    switch (key) {
      case 'timeUnitStartDate': {
        form.setFieldsValue({'startDateNumberOfHours': timeUnitToNumberOfHour(allValues.timeUnitStartDate)});
        break;
      }
      case 'timeUnitEndDate': {
        form.setFieldsValue({'endDateNumberOfHours': timeUnitToNumberOfHour(allValues.timeUnitEndDate)});
        break;
      }

      default:
        break;
    }
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

  if(usersResponse.data){
    return (

      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={initialValues}
        onFinish={timeKeeperAPIPost.run}
        form={form}
        onValuesChange={onValuesChange}
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
              <Form.Item
                label="Event type:"
                name="eventType"
                hasFeedback
                rules={[
                  {
                    required: true,
                  },
                ]}
              >
                <Radio.Group>
                  <Radio.Button value="COMPANY_EVENT">Company event</Radio.Button>
                  <Radio.Button value="USER_EVENT" disabled>User event</Radio.Button>
                </Radio.Group>
              </Form.Item>
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

              {/* ----- START-DATETIME & DURATION----- */}
              <Form.Item label="Start Date" name="startDateTime" rules={[{required: true}]}>
                <DatePicker format="DD-MM-YYYY" className="tk_DatePicker"/>
              </Form.Item>

              <Col className="gutter-row" span={9}>
                <Row>
                  <Form.Item name="timeUnitStartDate" label="Duration" rules={[{required: true}]}>
                    <Radio.Group>
                      <Radio value="DAY" className="tk-radio">Day</Radio>
                      <Radio value="HALFDAY" className="tk-radio">Half-day</Radio>
                      <Radio value="HOURLY">Hours</Radio>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnitStartDate !== curValues.timeUnitStartDate}>
                    {({getFieldValue}) => {
                      if(getFieldValue('timeUnitStartDate') === 'HOURLY') {
                        return (
                            <Form.Item name="startDateNumberOfHours" label="Duration:" rules={[{required: true}]}>
                              <Select showSearch>
                                {_.range(1, 9, 1).map(i =>
                                    <Option key={`option-hour-${i}`} value={i}>
                                      {i}
                                    </Option>)};
                              </Select>
                            </Form.Item>
                        );
                      } else {
                        return (
                            <Form.Item name="startDateNumberOfHours" noStyle={true}>
                              <Input hidden={true}/>
                            </Form.Item>
                        );
                      }
                    }}
                  </Form.Item>
                </Row>
              </Col>
              {/* ----- START-DATETIME & DURATION----- */}

              {/* ----- END-DATETIME & DURATION----- */}
              <Form.Item label="End Date" name="endDateTime" rules={[{required: true}]}>
                <DatePicker format="DD-MM-YYYY" className="tk_DatePicker"/>
              </Form.Item>

              <Col className="gutter-row" span={9}>
                <Row>
                  <Form.Item name="timeUnitEndDate" label="Duration" rules={[{required: true}]}>
                    <Radio.Group>
                      <Radio value="DAY" className="tk-radio">Day</Radio>
                      <Radio value="HALFDAY" className="tk-radio">Half-day</Radio>
                      <Radio value="HOURLY">Hours</Radio>
                    </Radio.Group>
                  </Form.Item>
                  <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnitEndDate !== curValues.timeUnitEndDate}>
                    {({getFieldValue}) => {
                      if(getFieldValue('timeUnitEndDate') === 'HOURLY') {
                        return (
                            <Form.Item name="endDateNumberOfHours" label="Duration:" rules={[{required: true}]}>
                              <Select showSearch>
                                {_.range(1, 9, 1).map(i =>
                                    <Option key={`option-hour-${i}`} value={i}>
                                      {i}
                                    </Option>)};
                              </Select>
                            </Form.Item>
                        );
                      } else {
                        return (
                            <Form.Item name="endDateNumberOfHours" noStyle={true}>
                              <Input hidden={true}/>
                            </Form.Item>
                        );
                      }
                    }}
                  </Form.Item>
                </Row>
              </Col>
              {/* ----- END-DATETIME & DURATION----- */}


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

  if (usersResponse.loading) {
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

  if (usersResponse.error) {
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