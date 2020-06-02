import React, {useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import {Button, Divider, Form, Input, Radio, Select} from "antd";
import TitleSection from "../Title/TitleSection";

const {Option} = Select;
const initialValues = (sheet, defaultDate) => {
  switch (sheet.timeUnit) {
    case "DAY":
      return {
        "comment": null,
        "billable": sheet.defaultIsBillable,
        "timeSheetId": sheet.id,
        "date": defaultDate || moment()
      };
    case "HALFDAY":
      return {
        "comment": null,
        "billable": sheet.defaultIsBillable,
        "timeSheetId": sheet.id,
        "date": defaultDate || moment(),
        "isMorning": true
      };
    case "HOURLY" :
      return {
        "comment": null,
        "billable": sheet.defaultIsBillable,
        "timeSheetId": sheet.id,
        "date": defaultDate || moment(),
        "startDateTime": null,
        "endDateTime": null
      };
    default:
      return <div>It seems like your timesheet is badly configured</div>
  }
};


// const update = ();

const AddEntry = ({selectedSheet, form, onFinish, defaultDate}) => {
  useEffect(() => {
    form.setFieldsValue({date: defaultDate})
  }, [defaultDate])
  return (
    <Form initialValues={initialValues(selectedSheet, defaultDate)} form={form} onFinish={onFinish.run}>
      <Form.Item label="Description" name="comment">
        <Input placeholder="Comment"/>
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
      "id": 1,
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
      "id": 2,
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
const TimeEntryForm = ({moment}) => {
  const [entryCreated, setEntryCreated] = useState(false);
  // TODO : call to get timesheets
  const SelectTimeSheet = () => <Form.Item name="timeSheetId">
    <Select>
      <Option value={null}/>
      {timeSheets.map(timeSheet => <Option value={timeSheet.id}>{timeSheet.project.name}</Option>)}
    </Select>
  </Form.Item>
  return (
    <div>
      <div>
        <p>{moment.format('ddd')}</p>
        <p>{moment.format('DD')}</p>
      </div>
      <h1>Day information</h1>

      <Divider/>
      <TitleSection title='Add task'/>
      <SelectTimeSheet/>
    </div>
  )
};

TimeEntryForm.propTypes = {
  moment: PropTypes.object.isRequired
};

export default TimeEntryForm;