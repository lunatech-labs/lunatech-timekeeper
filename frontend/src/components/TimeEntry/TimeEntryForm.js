import React, {useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import {Alert, Button, Col, Form, Input, message, Radio, Row, Select, Space, Spin} from 'antd';
import TitleSection from '../Title/TitleSection';
import {useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import '../Modal/ModalGeneral.less';
import NoDataMessage from '../NoDataMessage/NoDataMessage';
import ShowTimeEntry from './ShowTimeEntry';

const {Option} = Select;
const {TextArea} = Input;
const initialValues = (defaultDate) => {
  return {
    'comment': null,
    'billable': null,
    'timeSheetId': null,
    'date': defaultDate
  };
};
// Add the additional values
// Use the values of the form or initialize the values
const additionalValues = (timeUnit, defaultDate, allValues) => {
  switch (timeUnit) {
    case 'DAY': {
      return {};
    }
    case 'HALFDAY': {
      return {
        'isMorning': allValues.isMorning || true
      };
    }
    case 'HOURLY' : {
      const start = defaultDate.clone();
      start.set({
        hour: 9,
        minute: 0,
        second: 0
      });
      return {
        'startDateTime': allValues.startDateTime || start, //9 am by default
        'endDateTime': allValues.endDateTime || null
      };
    }
    default: {
      return {};
    }
  }
};

// Compute the url
const url = (form) => {
  const timeUnitToPrefix = (timeUnit) => {
    switch (timeUnit) {
      case 'DAY':
        return 'day';
      case 'HALFDAY':
        return 'half-a-day';
      case 'HOURLY':
        return 'hour';
      default:
        return '';
    }
  };
  const prefix = timeUnitToPrefix(form.getFieldValue('timeUnit'));
  const timeSheetId = form.getFieldValue('timeSheetId');
  return `/api/timeSheet/${timeSheetId}/timeEntry/${prefix}`;
};

const AddEntry = ({date, form, timeSheets, onSuccess, onCancel}) => {
  const [entryCreated, setEntryCreated] = useState(false);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();
  const timeKeeperAPIPost = useTimeKeeperAPIPost(url(form), (form => form), setEntryCreated);
  useEffect(() => {
    if (entryCreated) {
      message.success('Entry was created');
      onSuccess && onSuccess();
    }
  }, [entryCreated, onSuccess]);

  const {setFieldsValue} = form;
  useEffect(() => {
    setFieldsValue({date: date});
  }, [date, setFieldsValue]);

  // Update the values when a field changes
  const onValuesChange = (changedValues, allValues) => {
    const key = Object.keys(changedValues)[0];
    switch (key) {
      case 'timeSheetId' : {
        const timeSheet = timeSheets.find(item => item.id === changedValues.timeSheetId);
        setSelectedTimeSheet(timeSheet);
        if (timeSheet) {
          form.setFieldsValue({
            billable: timeSheet.defaultIsBillable,
            timeUnit: timeSheet.timeUnit
          });
        } else {
          form.setFieldsValue({
            billable: false,
            timeUnit: ''
          });
        }
        form.setFieldsValue(additionalValues(timeSheet.timeUnit, allValues.date, allValues));
        break;
      }
      case 'timeUnit': {
        form.setFieldsValue(additionalValues(changedValues.timeUnit, allValues.date, allValues));
        break;
      }
      case 'numberHours' : {
        const end = allValues.startDateTime.clone().add(changedValues.numberHours, 'hour');
        form.setFieldsValue({endDateTime: end});
        break;
      }
      default:
        break;
    }
  };
  AddEntry.propTypes = {
    date: PropTypes.object.isRequired,
    form: PropTypes.object.isRequired,
    timeSheets: PropTypes.array.isRequired,
    onSuccess: PropTypes.func,
    onCancel: PropTypes.func,
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

        <Form.Item label="Billable" name="billable" rules={[{required: true}]}>
          <Radio.Group>
            <Radio value={true}>Yes</Radio>
            <Radio value={false}>No</Radio>
          </Radio.Group>
        </Form.Item>

        <Row gutter={32}>
          <Col className="gutter-row" span={15}>
            <Form.Item shouldUpdate={(prevValues, currentValues) => prevValues.timeSheetId !== currentValues.timeSheetId}>
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

          <Col className="gutter-row" span={9}>
            {/*Additional Values : depends on the Time Unit*/}
            <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnit !== curValues.timeUnit}>
              {({getFieldValue}) => {
                switch (getFieldValue('timeUnit')) {
                  case 'DAY':
                    return null;
                  case 'HALFDAY' :
                    return (
                      <Form.Item name="isMorning" noStyle={true}>
                      </Form.Item>
                    );
                  case 'HOURLY':
                    return (
                      <div>
                        <Form.Item name="numberHours" label="Number of hours:" rules={[{required: true}]}>
                          <Input/>
                        </Form.Item>
                        <Form.Item name="startDateTime" noStyle={true}>
                        </Form.Item>
                        <Form.Item name="endDateTime" noStyle={true}>
                        </Form.Item>
                      </div>
                    );
                  default:
                    return null;
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

const TimeEntryForm = ({entries ,currentDay, form, onSuccess, onCancel, viewMode, setViewMode}) => {
  const timeSheets = useTimeKeeperAPI('/api/my/' + currentDay.year() + '?weekNumber=' + currentDay.isoWeek(), (form => form));
  if (timeSheets.loading) {
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

  if (timeSheets.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the data'
          type='error'
        />
      </React.Fragment>
    );
  }

  const Entries = (props) => {

    const entries = props.entries.map (
      entriesForDay => entriesForDay.map(entry => <ShowTimeEntry key={entry.id} entry={entry} />)
    );
    return (
      <div className="tk_TaskInfoList">
        {entries}
      </div>
    );
  };

  return (
    <div className="tk_ModalGen">
      <div className="tk_ModalTop">
        <div className="tk_ModalTopHead">
          <div>
            <p>{currentDay.format('ddd')}<br /><span>{currentDay.format('DD')}</span></p>
            <h1>Day information</h1>
          </div>
          {viewMode ? <Button type="link" onClick={() => setViewMode && setViewMode(false)}>Add task</Button> : ''}
        </div>
        <div className="tk_ModalTopBody">
          {entries.length === 0 ? <NoDataMessage message='No task for this day, there is still time to add one.'/> : <Entries entries={entries} />}
        </div>
      </div>
      {viewMode === false && <AddEntry date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess} onCancel={onCancel}/>}
    </div>
  );
};

TimeEntryForm.propTypes = {
  currentDay: PropTypes.object.isRequired,
  form: PropTypes.object,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
  viewMode: PropTypes.bool,
  setViewMode: PropTypes.func,
  entries: PropTypes.arrayOf(PropTypes.object)
};

export default TimeEntryForm;