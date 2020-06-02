import React, {useEffect, useState} from 'react';
import {Button, Col, Radio, Row, Select} from 'antd';
import CardWeekCalendar from '../Card/CardWeekCalendar';
import PropTypes from 'prop-types';
import PlusOutlined from '@ant-design/icons/lib/icons/PlusOutlined';
import LeftCircleOutlined from '@ant-design/icons/lib/icons/LeftCircleOutlined';
import RightCircleOutlined from '@ant-design/icons/lib/icons/RightCircleOutlined';

const moment = require('moment');

const renderWeekYear = (start, end) => {
  const panelFormatWithYear = 'DD MMM YYYY';
  const panelFormat = 'DD MMM';
  if ((start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // same month, same year => 22 - 25 May 2020
    return `${start.format('DD')} - ${end.format(panelFormatWithYear)}`;
  } else if ((!start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // different months, same year => 22 Jan - 01 Feb 2020
    return `${start.format(panelFormat)} - ${end.format(panelFormatWithYear)}`;
  } else {
    return `${start.format(panelFormatWithYear)} - ${end.format(panelFormatWithYear)}`;
  }
};

const renderWeekRange = (start, end) => {
  const panelFormat = 'DD MMM';
  if (start.isSame(end, 'month')) {
    return `${start.format('DD')} - ${end.format(panelFormat)}`;
  } else {
    return `${start.format(panelFormat)} - ${end.format(panelFormat)}`;
  }
};


const numberOfWeek = 30;
const weekRangeOfDate = (firstDay) => {
  const startOfCurrentWeek = firstDay || moment().startOf('week');
  return [...Array(numberOfWeek).keys()].map(i => {
    const toAdd = i - 7;
    const start = startOfCurrentWeek.clone().add(toAdd, 'week');
    const end = start.clone().endOf('week');
    return {
      id: toAdd,
      start: start,
      end: end
    };
  });
};


const WeekCalendar = (props) => {
  const [showButton, setShowButton] = useState(-1);
  const [weekSelected, setWeekSelected] = useState(0);
  const [weekRanges] = useState(weekRangeOfDate(props.firstDay));
  useEffect(() => {
    const weekRange = weekRangeOfDateMap.get(weekSelected);
    if (weekRange && props.onPanelChange) {
      const {id, start, end} = weekRange;
      props.onPanelChange(id, start, end);
    }
    // Im not interested of the changes of props.onPanelChange
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [weekSelected]);

  const weekRangeOfDateToMap = () => {
    const map = new Map();
    weekRanges.forEach((item) => map.set(item.id, item));
    return map;
  };
  const weekRangeOfDateMap = weekRangeOfDateToMap();

  const daysToData = () => {
    const daysOfWeek = [...Array(7).keys()].map(i => props.firstDay.clone().add(i, 'day'));
    return daysOfWeek.map(dayOfWeek => {
      const day = props.days.find(day => day.date.isSame(moment(dayOfWeek), 'day'));
      return {
        date: dayOfWeek,
        day: day,
      };
    });
  };
  const dateFormat = props.dateFormat || 'Do';
  const headerDateFormat = props.headerDateFormat || 'ddd';
  const dataByDays = daysToData();
  const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;
  const isDisabled = (item) => (item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(item.date));

  const WeekNavigator = () => {
    const weekRangeIds = weekRanges.map(weekRange => weekRange.id);
    const weekRange = weekRangeOfDateMap.get(weekSelected);
    const {start, end} = weekRange;
    const disableLeft = !weekRangeIds.includes(weekSelected - 1);
    const disableRight = !weekRangeIds.includes(weekSelected + 1);
    return (
      <div>
        <Button icon={<LeftCircleOutlined/>} disabled={disableLeft} shape='circle'
          onClick={() => setWeekSelected(weekSelected - 1)}/>
        {renderWeekYear(start, end)}
        <Button icon={<RightCircleOutlined/>} disabled={disableRight} shape='circle'
          onClick={() => setWeekSelected(weekSelected + 1)}/>
      </div>
    );
  };

  const WeekNavigatorSelect = () =>
    <Select onChange={id => setWeekSelected(id)} style={{width: 300}} defaultValue={0} value={weekSelected}>
      {weekRanges.map(({id, start, end}) => {
        return (
          <Select.Option key={`date-range-${id}`} value={id} disabled={id === weekSelected}>
            {renderWeekRange(start, end)}
          </Select.Option>
        );
      })}
    </Select>;

  const SelectionMode = () =>
    <Radio.Group defaultValue="Week">
      <Radio.Button value="Week">Week</Radio.Button>
      <Radio.Button value="Month">Month</Radio.Button>
    </Radio.Group>;

  return (
    <div>
      <div id='week-calendar-panel'>
        <WeekNavigator/>
        <WeekNavigatorSelect/>
        <SelectionMode/>
      </div>
      <Row gutter={[24, 16]}>
        {dataByDays.map((item, index) => {
          const renderDay = () => {
            if (item && item.day) {
              const {data, date, disabled} = item.day;
              return item && item.day && props.dateCellRender(data, date, disabled);
            }
          };
          return (
            <Col key={`day-card-${index}`} span={3}>
              <div>{item.date.format(headerDateFormat)}</div>
              <CardWeekCalendar
                disabled={isDisabled(item)} onMouseOver={() => setShowButton(index)}
                onMouseLeave={() => setShowButton(-1)}>
                <p>{item.date.format(dateFormat)}
                  {((props.hiddenButtons && showButton === index) || (!props.hiddenButtons)) &&
                    <Button type="primary" shape="circle" disabled={isDisabled(item)} icon={<PlusOutlined/>}/>}
                </p>
                {renderDay()}
              </CardWeekCalendar>
            </Col>
          );
        }
        )}
      </Row>
    </div>
  );
};

WeekCalendar.propTypes = {
  dateCellRender: PropTypes.func.isRequired, //(data, date, disabled) => node
  dateFormat: PropTypes.string,
  headerDateFormat: PropTypes.string,
  disabledWeekEnd: PropTypes.bool,
  firstDay: PropTypes.object.isRequired,
  days: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  hiddenButtons: PropTypes.bool,
  onPanelChange: PropTypes.func // (id, start, end) => void
};

export default WeekCalendar;