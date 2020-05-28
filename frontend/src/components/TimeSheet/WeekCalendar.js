import React from 'react';
import {Button, Col, Row} from "antd";
import CardWeekCalendar from "../Card/CardWeekCalendar";
import PropTypes from 'prop-types';
import {PlusCircleOutlined} from "@ant-design/icons";


const moment = require('moment');
const WeekCalendar = (props) => {

  const daysToData = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => props.firstDay.clone().add(i, 'days'));
    return daysOfWeek.map(dayOfWeek => {
      const day = props.days.find(day => day.date.isSame(moment(dayOfWeek), 'day'));
      return {
        date: dayOfWeek,
        day: day,
      }
    })
  };
  const dateFormat = props.dateFormat || 'Do';
  const headerDateFormat = props.headerDateFormat || 'ddd';
  const dataByDays = daysToData();
  const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;
  const isDisabled = (item) => (item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(item.date))
  return (
    <Row gutter={[24, 16]}>
      {dataByDays.map(item => {
          const renderDay = () => {
            if(item && item.day) {
              const {data, date, disabled} = item.day;
              return item && item.day && props.dateCellRender(data, date, disabled)
            }
          };
          return (
            <Col span={3}>
              <div>{item.date.format(headerDateFormat)}</div>
              <CardWeekCalendar disabled={isDisabled(item)}>
                <p>{item.date.format(dateFormat)} <Button type="primary" shape="circle" disabled={isDisabled(item)} icon={<PlusCircleOutlined />} /></p>
                {renderDay()}
              </CardWeekCalendar>
            </Col>
          )
        }
      )}
    </Row>

  )
};

WeekCalendar.propTypes = {
  dateCellRender: PropTypes.func.isRequired, //TODO : dayCellRender
  dateFormat: PropTypes.string,
  headerDateFormat: PropTypes.string,
  disabledWeekEnd: PropTypes.bool,
  firstDay: PropTypes.object.isRequired,
  // locale: TODO
  days: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired
};

export default WeekCalendar;