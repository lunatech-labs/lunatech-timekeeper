import {Radio} from 'antd';
import React from 'react';
import PropTypes from 'prop-types';
import './CalendarSelectionMode.less';

const CalendarSelectionMode = ({onChange}) =>
  <div className="tk_Calendar_SelectMode">
    <Radio.Group defaultValue="week" onChange={onChange}>
      <Radio.Button value="week">Week</Radio.Button>
      <Radio.Button value="month">Month</Radio.Button>
    </Radio.Group>
  </div>;

CalendarSelectionMode.propTypes = {
  onChange: PropTypes.func
};

export default CalendarSelectionMode;