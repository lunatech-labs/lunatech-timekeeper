import React from 'react';
import {Col, Row} from "antd";
import CardWeekCalendar from "../Card/CardWeekCalendar";
import PropTypes from 'prop-types';


const moment = require('moment');
const WeekCalendar = (props) => {

  const daysToData = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => props.firstDay.clone().add(i, 'days'));
    return daysOfWeek.map(dayOfWeek => {
      const data = props.days.find(day => day.moment.isSame(moment(dayOfWeek), 'day'));
      return {
        date: dayOfWeek,
        data: data && data.data
      }
    })
  };
  const dateFormat = props.dateFormat || 'Do';
  const headerDateFormat = props.headerDateFormat || 'ddd';
  const dataByDays = daysToData();
  const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;
  return (
    <Row gutter={[24, 16]}>
      {dataByDays.map(item =>
        <Col span={3}>
          {item.date.format(headerDateFormat)}
          <CardWeekCalendar disabled={(item.data && item.data.disabled) || isWeekEnd(item.date)}>
            {item.date.format(dateFormat)}
            {props.dateCellRender(item.data)}
          </CardWeekCalendar>
        </Col>
      )}
    </Row>

  )
};

WeekCalendar.propTypes = {
  dateCellRender: PropTypes.func.isRequired,
  dateFormat: PropTypes.string,
  headerDateFormat: PropTypes.string,
  disabledWeekEnd: PropTypes.bool,
  firstDay: PropTypes.object.isRequired,
  days: PropTypes.arrayOf(
    PropTypes.shape({
      moment: PropTypes.object.isRequired,
      disabled: PropTypes.bool,
      data: PropTypes.object
    })
  ).isRequired
};

export default WeekCalendar;