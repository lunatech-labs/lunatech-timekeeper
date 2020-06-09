import React, {useEffect, useState} from 'react';
import {Button, Radio, Select} from 'antd';
import CardWeekCalendar from '../Card/CardWeekCalendar';
import PropTypes from 'prop-types';
import {LeftOutlined, PlusOutlined, RightOutlined} from '@ant-design/icons';
import './WeekCalendar.less';

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
  const startOfCurrentWeek = firstDay || moment().utc().startOf('week');
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
      const day = props.days.find(day => day.date.isSame(moment(dayOfWeek).utc(), 'day'));
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
        <Button icon={<LeftOutlined/>} disabled={disableLeft} shape='circle' onClick={() => setWeekSelected(weekSelected - 1)}/>
        <Button icon={<RightOutlined/>} disabled={disableRight} shape='circle' onClick={() => setWeekSelected(weekSelected + 1)}/>
        <p>{renderWeekYear(start, end)}</p>
      </div>
    );
  };

  const WeekNavigatorSelect = () =>
    <Select onChange={id => setWeekSelected(id)} defaultValue={0} value={weekSelected}>
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
    <div id="tk_WeekCalendar">
      <div id="tk_WeekCalendar_Head">
        <WeekNavigator/>
        <div>
          <WeekNavigatorSelect/>
          <SelectionMode/>
        </div>
      </div>

      <div id="tk_WeekCalendar_Body">
        {dataByDays.map((item, index) => {
          const renderDay = () => {
            if (item && item.day) {
              const {data, date, disabled} = item.day;
              return item && item.day && props.dateCellRender(data, date, disabled);
            }
          };

          const today = (day) => {
            return moment().isSame(day, 'day');
          };

          return (
            <div className="tk_WeekCalendar_Day" key={`day-card-${index}`}>
              <p>{item.date.format(headerDateFormat)}</p>
              <CardWeekCalendar
                onMouseOver={() => setShowButton(index)}
                onMouseLeave={() => setShowButton(-1)}>
                <div className="tk_CardWeekCalendar_Head">
                  <p className={today(moment(item.date)) ? 'tk_CurrentDay':''}>{item.date.format(dateFormat)}</p>
                  {((props.hiddenButtons && showButton === index) || (!props.hiddenButtons)) &&
                  <Button
                    shape="circle"
                    disabled={isDisabled(item)}
                    icon={<PlusOutlined/>}
                    onClick={(e) => props.onClickAddTask && props.onClickAddTask(e, item.date)}/>}
                </div>
                <div className="tk_CardWeekCalendar_Body" disabled={isDisabled(item)}>
                  {renderDay()}
                </div>
              </CardWeekCalendar>
            </div>
          );
        })}
      </div>
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
  onClickAddTask: PropTypes.func, // (event, moment) => void
  onPanelChange: PropTypes.func // (id, start, end) => void
};

export default WeekCalendar;