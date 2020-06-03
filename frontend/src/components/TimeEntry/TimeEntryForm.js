import React, {useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Form, Input, message, Radio, Select} from "antd";
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
// Add the additional values
// Use the values of the form or initialize the values
const additionalValues = (timeUnit, defaultDate, allValues) => {
  switch (timeUnit) {
    case "DAY":
      return {};
    case "HALFDAY":
      return {
        "isMorning": allValues.isMorning || true
      };
    case "HOURLY" :
      const start = defaultDate.clone();
      start.set({
        hour: 9,
        minute: 0,
        second: 0
      });
      return {
        "startDateTime": allValues.startDateTime || start, //9 am by default
        "endDateTime": allValues.endDateTime || null
      };
    default:
      return {};
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

const AddEntry = ({date, form, timeSheets, onSuccess}) => {
  const [entryCreated, setEntryCreated] = useState(false);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();
  const timeKeeperAPIPost = useTimeKeeperAPIPost(url(form), (form => form), setEntryCreated);
  useEffect(() => {
    if (entryCreated) {
      message.success('Entry was created');
      onSuccess();
    }
  }, [entryCreated, onSuccess]);
  useEffect(() => {
    form.setFieldsValue({date: date});
  }, [date]);

  // Update the values when a field changes
  const onValuesChange = (changedValues, allValues) => {
    const key = Object.keys(changedValues)[0];
    switch (key) {
      case 'timeSheetId' :
        const timeSheet = timeSheets.find(item => item.id === changedValues.timeSheetId);
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
        form.setFieldsValue(additionalValues(timeSheet.timeUnit, allValues.date, allValues));
        break;
      case 'timeUnit':
        form.setFieldsValue(additionalValues(changedValues.timeUnit, allValues.date, allValues));
        break;
      case 'numberHours' :
        const end = allValues.startDateTime.clone().add(changedValues.numberHours, 'hour');
        form.setFieldsValue({endDateTime: end})
        break;
      default:
        break;
    }
  };
  return (
    <Form initialValues={initialValues(date)} form={form}
          onFieldsChange={(changedFields, allFields) => console.log(changedFields, allFields)}
          onFinish={timeKeeperAPIPost.run}
          onValuesChange={onValuesChange}
    >

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
        <Select>
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
              <Radio.Group>
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
                </Form.Item>
              );
            case 'HOURLY':
              return (
                <div>
                  <Form.Item name="numberHours" label="Number of hours">
                    <Input/>
                  </Form.Item>
                  <Form.Item name="startDateTime">
                  </Form.Item>
                  <Form.Item name="endDateTime">
                  </Form.Item>
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
const TimeEntryForm = ({moment, form, onSuccess}) => {
  return (
    <div>
      <div>
        <p>{moment.format('ddd')}</p>
        <p>{moment.format('DD')}</p>
      </div>
      <h1>Day information</h1>

      <Divider/>
      <TitleSection title='Add task'/>
      <AddEntry date={moment} form={form} timeSheets={timeSheets} onSuccess={onSuccess}/>
    </div>
  )
};

TimeEntryForm.propTypes = {
  moment: PropTypes.object.isRequired
};

export default TimeEntryForm;