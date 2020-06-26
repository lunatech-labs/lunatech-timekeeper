import {Radio} from "antd";
import React from "react";

const CalendarSelectionMode = ({onChange}) =>
  <Radio.Group defaultValue="week" onChange={onChange}>
    <Radio.Button value="week">Week</Radio.Button>
    <Radio.Button value="month">Month</Radio.Button>
  </Radio.Group>;

export default CalendarSelectionMode