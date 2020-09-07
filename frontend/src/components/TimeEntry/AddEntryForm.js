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
import {useTimeKeeperAPIPost} from '../../utils/services';
import {Button, Col, Form, Input, message, Radio, Row, Select, Space} from 'antd';
import PropTypes from 'prop-types';
import TitleSection from '../Title/TitleSection';
import './AddEntryForm.less';
import SelectHoursComponent from './SelectHoursComponent';
import {isTimeSheetDisabled} from '../../utils/timesheetUtils';

const {Option} = Select;
const {TextArea} = Input;

const initialValues = (defaultDate) => {
  return {
    'comment': null,
    'timeSheetId': null,
    'startDate': defaultDate,
    'numberOfHours':1
  };
};

// Add the additional values
// Use the values of the form or initialize the values
const additionalValues = (timeUnit, defaultDate, allValues) => {
  switch (timeUnit) {
    case 'DAY': {
      return {
        'startDate': allValues.startDate,
        'numberOfHours': 8
      };
    }
    case 'HALFDAY': {
      return {
        'startDate': allValues.startDate,
        'numberOfHours': 4
      };
    }
    case 'HOURLY' : {
      return {
        'startDate': allValues.startDate,
        'numberOfHours': null
      };
    }
    default: {
      return {};
    }
  }
};

// Compute the url
const url = (form) => {
  const timeSheetId = form.getFieldValue('timeSheetId');
  return `/api/timeSheet/${timeSheetId}/timeEntry`;
};

const AddEntryForm = ({date, form, timeSheets, onSuccess, onCancel, numberOfHoursForDay}) => {
  const [entryCreated, setEntryCreated] = useState(false);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();
  const timeKeeperAPIPost = useTimeKeeperAPIPost(url(form), (form => form), setEntryCreated);
  useEffect(() => {
    if (entryCreated) {
      message.success('Entry was created');
      onSuccess && onSuccess();
    }
  }, [entryCreated, onSuccess]);

  const {resetFields} = form;

  useEffect(()=>{
    resetFields();
  }, [resetFields]);

  // Update the values when a field changes
  const onValuesChange = (changedValues, allValues) => {
    const key = Object.keys(changedValues)[0];
    switch (key) {
      case 'timeSheetId' : {
        const timeSheet = timeSheets.find(item => item.id === changedValues.timeSheetId);
        setSelectedTimeSheet(timeSheet);
        const timeUnit = timeSheet ? timeSheet.timeUnit : '';
        form.setFieldsValue({timeUnit});
        form.setFieldsValue(additionalValues(timeSheet.timeUnit, allValues.startDate, allValues));
        break;
      }
      case 'timeUnit': {
        form.setFieldsValue(additionalValues(changedValues.timeUnit, allValues.startDate, allValues));
        break;
      }
      default:
        break;
    }
  };

  return (
    <div className="tk_ModalBottom">
      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={initialValues(date)}
        form={form}
        onFinish={timeKeeperAPIPost.run}
        onValuesChange={onValuesChange}
      >
        <TitleSection title='Add task'/>
        <Form.Item name="startDate" noStyle={true}>
          <Input hidden={true}/>
        </Form.Item>

        <Form.Item label="Description:" name="comment" rules={[{required: true}]}>
          <TextArea rows={2} placeholder="What did you work on ?"/>
        </Form.Item>

        <Form.Item label="Select a project:" name="timeSheetId" rules={[{required: true}]}>
          <Select>
            <Option value={null}/>
            {timeSheets.map(timeSheet => <Option disabled={isTimeSheetDisabled(timeSheet, numberOfHoursForDay)} key={`select-timesheet-${timeSheet.id}`}
              value={timeSheet.id}>{timeSheet.project.name}</Option>)}
          </Select>
        </Form.Item>

        <Row gutter={32}>
          <Col className="gutter-row" span={15}>
            <Form.Item shouldUpdate={(prevValues, currentValues) => prevValues.timeSheetId !== currentValues.timeSheetId}>
              {() => {
                const timeUnit = selectedTimeSheet && selectedTimeSheet.timeUnit;
                const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
                const halfDayDisabled = (timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY') || (numberOfHoursForDay && numberOfHoursForDay > 4);
                const dayDisabled = numberOfHoursForDay && numberOfHoursForDay > 0;
                return (
                  <Form.Item name="timeUnit" label="Logged time:" rules={[{required: true}]}>
                    <Radio.Group>
                      <Radio value="DAY" className="tk-radio" disabled={dayDisabled}>Day</Radio>
                      <Radio value="HALFDAY" className="tk-radio" disabled={halfDayDisabled}>Half-day</Radio>
                      <Radio value="HOURLY" disabled={hourDisabled}>Hours</Radio>
                    </Radio.Group>
                  </Form.Item>
                );
              }}
            </Form.Item>
          </Col>

          <Col className="gutter-row" span={9}>
            {/*Additional Values : depends on the Time Unit*/}
            <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnit !== curValues.timeUnit}>
              {({getFieldValue}) => {
                switch (getFieldValue('timeUnit')) {
                  case 'HOURLY':
                    return (
                      <div>
                        {<SelectHoursComponent numberOfHoursForDay={numberOfHoursForDay} />}
                      </div>
                    );
                  default:
                    return (
                      <Form.Item name="numberOfHours" noStyle={true}>
                        <Input hidden={true}/>
                      </Form.Item>
                    );
                }
              }}
            </Form.Item>
          </Col>
        </Row>

        <Space className="tk_JcFe" size="middle" align="center">
          <Button id="btnCancelAddEntry" className="tk_Btn tk_BtnSecondary" key="cancelLink"
            onClick={e => onCancel && onCancel(e)}>Cancel</Button>
          <Button id="btnSaveEntry" className="tk_Btn tk_BtnPrimary" htmlType="submit">Save task</Button>
        </Space>
      </Form>
    </div>
  );
};
AddEntryForm.propTypes = {
  date: PropTypes.object.isRequired,
  form: PropTypes.object.isRequired,
  timeSheets: PropTypes.array.isRequired,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
  numberOfHoursForDay: PropTypes.number.isRequired
};

export default AddEntryForm;