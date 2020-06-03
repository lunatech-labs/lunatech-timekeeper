import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Form, Input, Radio, Select} from "antd";
import TitleSection from "../Title/TitleSection";
import {useTimeKeeperAPIPost} from "../../utils/services";

const {Option} = Select;
const {TextArea} = Input;

const initialValues = (defaultDate) => {
  return {
    "comment": null,
    "billable": null,
    "timeSheetId": null,
    "date": defaultDate
  }
};

const additionalValues = (timeUnit) => {
  switch (timeUnit) {
    case "DAY":
      return {};
    case "HALFDAY":
      return {
        "isMorning": true
      };
    case "HOURLY" :
      return {
        "startDateTime": null, //9h
        "endDateTime": null // To set
      };
    default:
      return {};
  }
}

const AddEntry = ({date, form, timeSheets}) => {
  const [entryCreated, setEntryCreated] = useState(false);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();
  const url = () => {
    const timeUnitToPrefix = (timeUnit) => {
      switch (timeUnit) {
        case 'DAY':
          return 'day';
        case 'HALFDAY':
          return 'half-a-day';
        case 'HOURLY':
          return 'hour';
      }
    };
    const prefix = timeUnitToPrefix(form.getFieldValue('timeUnit'));
    const timeSheetId = form.getFieldValue('timeSheetId');
    console.log(timeSheetId)
    console.log(`==================`)
    console.log(`/api/timeSheet/${timeSheetId}/timeEntry/${prefix}`)
    return `/api/timeSheet/${timeSheetId}/timeEntry/${prefix}`;
  };
  const timeKeeperAPIPost = useTimeKeeperAPIPost(url(), (form => form), setEntryCreated);

  const onChangeTimeSheet = (id) => {
    const timeSheet = timeSheets.find(item => item.id === id);
    setSelectedTimeSheet(timeSheet);
    if (timeSheet) {
      form.setFieldsValue({
        billable: timeSheet.defaultIsBillable,
        timeUnit: timeSheet.timeUnit
      })
    } else {
      form.setFieldsValue({
        billable: false,
        timeUnit: ''
      })
    }
  };
  const onChangeTimeUnit = (timeUnit) => {
    form.setFieldsValue(additionalValues(timeUnit));
  };
  return (
    <Form initialValues={initialValues(date)} form={form}
          onFieldsChange={(changedFields, allFields) => console.log(changedFields, allFields)}
          onFinish={timeKeeperAPIPost.run}>
      <Form.Item name="date">

      </Form.Item>

      <Form.Item label="Description" name="comment">
        <TextArea
          rows={2}
          placeholder="What did you work on ?"
        />
      </Form.Item>

      <Form.Item
        label="Project"
        name="timeSheetId">
        <Select onChange={onChangeTimeSheet}>
          <Option value={null}/>
          {timeSheets.map(timeSheet => <Option value={timeSheet.id}>{timeSheet.project.name}</Option>)}
        </Select>
      </Form.Item>
      <Form.Item
        label="Billable"
        name="billable"
      >
        <Radio.Group>
          <Radio value={true}>Yes</Radio>
          <Radio value={false}>No</Radio>
        </Radio.Group>
      </Form.Item>
      <Form.Item
        shouldUpdate={(prevValues, currentValues) => prevValues.timeSheetId !== currentValues.timeSheetId}
      >
        {() => {
          const timeUnit = selectedTimeSheet && selectedTimeSheet.timeUnit;
          const hourDisabled = timeUnit && timeUnit !== 'HOURLY';
          const halfDayDisabled = timeUnit && timeUnit !== 'HOURLY' && timeUnit !== 'HALFDAY';
          return (
            <Form.Item name="timeUnit" label="Time unit">
              <Radio.Group onChange={onChangeTimeUnit}>
                <Radio.Button value="HOURLY" disabled={hourDisabled}>Hours</Radio.Button>
                <Radio.Button value="HALFDAY" disabled={halfDayDisabled}>Half-day</Radio.Button>
                <Radio.Button value="DAY">Day</Radio.Button>
              </Radio.Group>
            </Form.Item>
          );
        }}
      </Form.Item>
      {/*Additional Values : depends on the Time Unit*/}
      <Form.Item shouldUpdate={(prevValues, curValues) => prevValues.timeUnit !== curValues.timeUnit}>
        {({getFieldValue}) => {
          switch (getFieldValue('timeUnit')) {
            case 'DAY':
              return null;
            case 'HALFDAY' :
              return (
                <Form.Item name="isMorning" label="Morning">
                  <Input/>
                </Form.Item>
              );
            case 'HOURLY':
              return (
                <div>
                  <Form.Item name="numberHours" label="Number of hours">
                    <Input/>
                  </Form.Item>
                  {/*<Form.Item name="startDateTime">
                </Form.Item>
                  <Form.Item name="endDateTime">
                  </Form.Item>*/}
                </div>

              )
          }

          return (
            getFieldValue('timeUnit') === 'HOURLY' &&
            <Form.Item name="numberHours" label="Number of hours">
              <Input/>
            </Form.Item>
          );
        }}
      </Form.Item>

      <Button htmlType="submit">
        Best bouton ever
      </Button>
    </Form>
  );
};


const timeSheets =
  [
    {
      "active": true,
      "defaultIsBillable": true,
      "durationUnit": "Day",
      "expirationDate": "2020-05-18",
      "id": 8,
      "maxDuration": 0,
      "ownerId": 10,
      "project": {
        "billable": true,
        "client": {
          "id": 1,
          "name": "Disney"
        },
        "description": "The disney's project",
        "id": 0,
        "name": "Disney's website",
        "publicAccess": true,
        //Useless
        "users": [
          {
            "id": 0,
            "manager": true,
            "name": "string",
            "picture": "string"
          }
        ]
      },
      "timeUnit": "HALFDAY",
      "valid": true
    },
    {
      "active": true,
      "defaultIsBillable": false,
      "durationUnit": "Hour",
      "expirationDate": "2020-05-18",
      "id": 9,
      "maxDuration": 0,
      "ownerId": 10,
      "project": {
        "billable": true,
        "client": {
          "id": 1,
          "name": "Disney"
        },
        "description": "The Darva's project",
        "id": 0,
        "name": "Sinapps",
        "publicAccess": true,
        //Useless
        "users": [
          {
            "id": 0,
            "manager": true,
            "name": "string",
            "picture": "string"
          }
        ]
      },
      "timeUnit": "HOURLY",
      "valid": true
    }
  ];
const moment = require('moment');
const TimeEntryForm = ({moment, form}) => {
  // TODO : call to get timesheets
  const SelectTimeSheet = () => <Form.Item name="timeSheetId">
    <Select>
      <Option value={null}/>
      {timeSheets.map(timeSheet => <Option value={timeSheet.id}>{timeSheet.project.name}</Option>)}
    </Select>
  </Form.Item>;
  return (
    <div>
      <div>
        <p>{moment.format('ddd')}</p>
        <p>{moment.format('DD')}</p>
      </div>
      <h1>Day information</h1>

      <Divider/>
      <TitleSection title='Add task'/>
      <AddEntry date={moment} form={form} timeSheets={timeSheets}/>
    </div>
  )
};

TimeEntryForm.propTypes = {
  moment: PropTypes.object.isRequired
};

export default TimeEntryForm;