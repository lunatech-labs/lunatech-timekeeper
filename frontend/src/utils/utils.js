import React from 'react';
import {Select} from 'antd';
var _ = require('lodash');

const {Option} = Select;

//Returns the options list of hours for a day with some of them may be disabled to be less than 8hrs of timeEntry for a day
export const hoursOptions = (numberOfHoursForDay, entryDuration) => {
  const hourDisabled = (hour) => {
    if(entryDuration){
      return parseInt(hour) + (parseInt(numberOfHoursForDay) - entryDuration) > 8;
    }
    return parseInt(hour) + parseInt(numberOfHoursForDay) > 8;
  };
  return _.range(1, 9, 1).map(i => <Option key={`option-hour-${i}`} disabled={hourDisabled(i)} value={i} >{i}</Option>);
};

//Return the className of disabled radio button in EventForm
export const radioDisabledClassName = (disabled) => {
  if(disabled) {
    return 'tk-radio-disabled';
  }
};