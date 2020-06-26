import {Radio} from 'antd';
import React from 'react';
import PropTypes from 'prop-types';
const CalendarSelectionMode = ({onChange}) =>
  <Radio.Group defaultValue="week" onChange={onChange}>
    <Radio.Button value="week">Week</Radio.Button>
    <Radio.Button value="month">Month</Radio.Button>
  </Radio.Group>;

CalendarSelectionMode.propTypes = {
  onChange: PropTypes.func
};

export default CalendarSelectionMode;