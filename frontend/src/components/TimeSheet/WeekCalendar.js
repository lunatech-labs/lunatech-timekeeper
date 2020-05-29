import React, {useState} from 'react';
import {Button, Col, Row, Select} from "antd";
import CardWeekCalendar from "../Card/CardWeekCalendar";
import PropTypes from 'prop-types';
import PlusOutlined from "@ant-design/icons/lib/icons/PlusOutlined";


const moment = require('moment');
const WeekCalendar = (props) => {
  const [showButton, setShowButton] = useState(-1);
  const weekRangeOfDate = () => {
    const startOfCurrentWeek = moment().startOf('week').add(1, 'day');
    return [...Array(30).keys()].map(i => {
      const toAdd = i - 7;
      const start = startOfCurrentWeek.clone().add(toAdd, 'week');
      const end = start.clone().endOf('week').add(1, 'day');
      return {
        start: start,
        end: end
      }
    })
  };
  const renderWeekRange = (start, end) => {
    const panelFormat = "DD MMM";
    if (start.isSame(end, 'month')) {
      return `${start.format("DD")} - ${end.format(panelFormat)}`
    } else {
      return `${start.format(panelFormat)} - ${end.format(panelFormat)}`
    }
  };
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
  const isDisabled = (item) => (item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(item.date));

  const weeksPanel = weekRangeOfDate();
  return (
    <div>
      <div id='week-calendar-panel'>
        <Select>
          <option>Select a range</option>
          {weeksPanel.map(({start, end}) => {
            return (
              <option>
                {renderWeekRange(start, end)}
              </option>
            )
          })}
        </Select>
      </div>
      <Row gutter={[24, 16]}>
        {dataByDays.map((item, index) => {
            const renderDay = () => {
              if (item && item.day) {
                const {data, date, disabled} = item.day;
                return item && item.day && props.dateCellRender(data, date, disabled)
              }
            };
            return (
              <Col span={3}>
                <div>{item.date.format(headerDateFormat)}</div>
                <CardWeekCalendar
                  disabled={isDisabled(item)} onMouseOver={() => setShowButton(index)}
                  onMouseLeave={() => setShowButton(-1)}>
                  <p>{item.date.format(dateFormat)}
                    {((props.hiddenButtons && showButton === index) || (!props.hiddenButtons)) &&
                    <Button type="primary" shape="circle" disabled={isDisabled(item)} icon={<PlusOutlined/>}/>}
                    {/*<Button type="primary" disabled={isDisabled(item)} shape="circle" icon={<PlusOutlined/>}/>*/}
                  </p>
                  {renderDay()}
                </CardWeekCalendar>
              </Col>
            )
          }
        )}
      </Row>
    </div>

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
  ).isRequired,
  hiddenButtons: PropTypes.bool,
  onPanelChange: PropTypes.func
};

export default WeekCalendar;