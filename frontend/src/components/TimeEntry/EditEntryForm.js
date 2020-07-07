import React, {useEffect, useState} from 'react';
import {useTimeKeeperAPIPut} from '../../utils/services';
import {Button, Col, Form, Input, message, Radio, Row, Select, Space} from 'antd';
import PropTypes from 'prop-types';
import TitleSection from '../Title/TitleSection';
import moment from 'moment';
import {computeNumberOfHours} from '../../utils/momentUtils';

const {Option} = Select;
const {TextArea} = Input;


const computeTimeUnit = (numberHours) => {
  switch (numberHours) {
    case 4:
      return 'HALFDAY';
    case 8:
      return 'DAY';
    default:
      return 'HOURLY';
  }
};

const initialValues = (defaultDate, entry, timeSheetId) => {
  const newEntry = {
    ...entry,
    startDateTime: moment.utc(entry.startDateTime),
    endDateTime: moment.utc(entry.endDateTime)
  };
  const numberOfHours = computeNumberOfHours(newEntry.startDateTime, newEntry.endDateTime);
  return {
    'comment': newEntry.comment,
    'timeSheetId': timeSheetId,
    'date': moment.utc(newEntry.startDateTime, 'YYYY-MM-DD'),
    'startDateTime': newEntry.startDateTime,
    'endDateTime': newEntry.endDateTime,
    'timeUnit' : computeTimeUnit(numberOfHours),
    'numberHours': numberOfHours
  };
};

const updatedValues = (timeUnit, allValues, start) => {
  switch (timeUnit) {
    case 'DAY': {
      return {
        'startDateTime': allValues.startDateTime || start, //9 am by default
        'numberHours': 8
      };
    }
    case 'HALFDAY': {
      return {
        'startDateTime': allValues.startDateTime || start, //9 am by default
        'numberHours': 4
      };
    }
    case 'HOURLY' : {
      return {
        'startDateTime': allValues.startDateTime || start, //9 am by default
        'numberHours': null
      };
    }
    default: {
      return {};
    }
  }
};

// Add the additional values
// Use the values of the form or initialize the values
const additionalValues = (timeUnit, defaultDate, allValues) => {
  const start = defaultDate.clone();
  start.set({
    hour: 9,
    minute: 0,
    second: 0
  });
  return updatedValues(timeUnit, allValues, start);
};

// Compute the url
const urlEdition = (form, timeEntryId) => {
  const timeSheetId = form.getFieldValue('timeSheetId');
  return `/api/timeSheet/${timeSheetId}/timeEntry/${timeEntryId}`;
};

const EditEntryForm = ({date, form, timeSheets, onSuccess, onCancel, entry}) => {
  const [entryUpdated, setEntryUpdated] = useState(false);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();
  const timeKeeperAPIPut = useTimeKeeperAPIPut(urlEdition(form, entry.id), (form => form), setEntryUpdated);
  useEffect(() => {
    if (entryUpdated) {
      message.success('Entry was updated');
      onSuccess && onSuccess();
    }
  }, [entryUpdated, onSuccess]);

  const {resetFields} = form;

  useEffect(()=>{
    resetFields();
  }, [resetFields, entry.id]);

  // Update the values when a field changes
  const onValuesChange = (changedValues, allValues) => {
    const key = Object.keys(changedValues)[0];
    switch (key) {
      case 'timeSheetId' : {
        const timeSheet = timeSheets.find(item => item.id === changedValues.timeSheetId);
        setSelectedTimeSheet(timeSheet);
        form.setFieldsValue(timeSheet ? {timeUnit: timeSheet.timeUnit} : {timeUnit: ''});
        form.setFieldsValue(additionalValues(timeSheet.timeUnit, allValues.date, allValues));
        break;
      }
      case 'timeUnit': {
        form.setFieldsValue(additionalValues(changedValues.timeUnit, allValues.date, allValues));
        break;
      }
      default:
        break;
    }
  };

  const findTimeSheetId = (entryId, timeSheets) => {
    return timeSheets.find(timeSheet => !!timeSheet.entries.find(entry => entry.id === entryId)).id;
  };

  return (
    <div className="tk_ModalBottom">
      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={initialValues(date, entry, findTimeSheetId(entry.id, timeSheets))}
        form={form}
        onFinish={timeKeeperAPIPut.run}
        onValuesChange={onValuesChange}
      >
        <TitleSection title='Edit task'/>
        <Form.Item name="date" noStyle={true}>
        </Form.Item>

        <Form.Item label="Description:" name="comment" rules={[{required: true}]}>
          <TextArea rows={2} placeholder="What did you work on ?"/>
        </Form.Item>

        <Form.Item label="Select a project:" name="timeSheetId" rules={[{required: true}]}>
          <Select>
            <Option value={null}/>
            {timeSheets.map(timeSheet => <Option key={`select-timesheet-${timeSheet.id}`}
              value={timeSheet.id}>{timeSheet.project.name}</Option>)}
          </Select>
        </Form.Item>

        <Row gutter={32}>
          <Col className="gutter-row" span={15}>
            <Form.Item
              shouldUpdate={(prevValues, currentValues) => prevValues.timeSheetId !== currentValues.timeSheetId}>
              {() => {
                const timeUnit = selectedTimeSheet && selectedTimeSheet.timeUnit;
                const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
                const halfDayDisabled = timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY';
                return (
                  <Form.Item name="timeUnit" label="Logged time:" rules={[{required: true}]}>
                    <Radio.Group>
                      <Radio value="DAY">Day</Radio>
                      <Radio value="HALFDAY" disabled={halfDayDisabled}>Half-day</Radio>
                      <Radio value="HOURLY" disabled={hourDisabled}>Hours</Radio>
                    </Radio.Group>
                  </Form.Item>
                );
              }}
            </Form.Item>
          </Col>

          <Form.Item name="startDateTime" noStyle={true}>
          </Form.Item>

          <Col className="gutter-row" span={9}>
            {/*Additional Values : depends on the Time Unit*/}
            <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnit !== curValues.timeUnit}>
              {({getFieldValue}) => {
                switch (getFieldValue('timeUnit')) {
                  case 'HOURLY':
                    return (
                      <div>
                        <Form.Item name="numberHours" label="Number of hours:"
                          rules={[{required: true}]}>
                          <Input/>
                        </Form.Item>
                      </div>
                    );
                  default:
                    return (
                      <Form.Item name="numberHours" noStyle={true}>
                        <Input hidden={true}/>
                      </Form.Item>
                    );
                }
              }}
            </Form.Item>
          </Col>
        </Row>

        <Space className="tk_JcFe" size="middle" align="center">
          <Button id="tk_Btn" className="tk_BtnSecondary" key="cancelLink"
            onClick={e => onCancel && onCancel(e)}>Cancel</Button>
          <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Save task</Button>
        </Space>
      </Form>
    </div>
  );
};
EditEntryForm.propTypes = {
  date: PropTypes.object.isRequired,
  form: PropTypes.object.isRequired,
  timeSheets: PropTypes.array.isRequired,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
  entry: PropTypes.shape({
    id: PropTypes.number.isRequired,
    comment: PropTypes.string.isRequired,
    billable: PropTypes.bool,
    timeSheetId: PropTypes.number.isRequired,
    isMorning: PropTypes.bool.isRequired,
    startDateTime: PropTypes.object.isRequired,
    endDateTime: PropTypes.object.isRequired,
  }).isRequired,
  timeSheetId: PropTypes.number.isRequired
};
export default EditEntryForm;