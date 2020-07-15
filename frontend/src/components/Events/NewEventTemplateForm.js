import React, {useEffect, useState} from 'react';
import {sortListByName, useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import {Alert, Button, Col, Form, Input, message, Row, Space, Spin, DatePicker} from 'antd';
import './NewEventTemplateForm.less';
import '../../components/Button/BtnGeneral.less';
import {Link, Redirect} from 'react-router-dom';
import TitleSection from '../../components/Title/TitleSection';
import moment from 'moment';
import UserTreeData from './UserTreeData';

const {TextArea} = Input;
const { RangePicker } = DatePicker;

const NewEventTemplateForm = () => {
  const [eventTemplateCreated, setEventTemplateCreated] = useState(false);

  const formDataToEventRequest = (formData) => ({
    name: formData.name,
    description: formData.description,
    startDateTime: formData.eventDateTime[0],
    endDateTime: formData.eventDateTime[1],
    attendees: []
  });
  const usersResponse = useTimeKeeperAPI('/api/users');
  const eventsResponse = useTimeKeeperAPI('/api/users');
  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/events', (form => form) , setEventTemplateCreated, formDataToEventRequest);

  const [form] = Form.useForm();

  const initialValues = {
    name: '',
    description: '',
    eventDateTime: [moment.utc('9:00 AM', 'LT'),moment.utc('9:00 AM', 'LT')],
    attendees: []
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

  if (timeKeeperAPIPost.error) {
    const {response} = timeKeeperAPIPost.error;
    const {status, url} = response;
    const errMsg = `Server error HTTP Code:${status}  for url: ${url}`;
    return (
      <React.Fragment>
        <Alert
          message="Unable to save the new Event"
          description={errMsg}
          type="error"
          closable
          style={{marginBottom: 10}}
        />
      </React.Fragment>
    );
  }

  const range = (start, end) => {
    const result = [];
    for (let i = start; i < end; i++) {
      result.push(i);
    }
    return result;
  };

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

  if(eventsResponse.data && usersResponse.data){
    const eventsName = eventsResponse.data.map(event => event.name.toLowerCase().trim());
    return (
      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={initialValues}
        onFinish={timeKeeperAPIPost.run}
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
                  () => ({
                    validator(rule, value) {
                      const name = value.toLowerCase().trim();
                      if (!eventsName.includes(name)) {
                        return Promise.resolve();
                      }
                      return Promise.reject('An event already use this name');
                    },
                  }),
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