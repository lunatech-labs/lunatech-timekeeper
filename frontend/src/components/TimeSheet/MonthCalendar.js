import React from 'react';
import {Button, Calendar, Select} from 'antd';
import PropTypes from 'prop-types';
import {LeftOutlined, PlusOutlined, RightOutlined} from '@ant-design/icons';
import moment from 'moment';
import './MonthCalendar.less';

const {Option} = Select;

const SelectYear = ({value, onChange}) => {
  const numberOfYear = 20; // It is like ant.d
  const options = [...Array(numberOfYear).keys()].map(i => {
    const toAdd = i - 10;
    const nextDate = value.clone().add(toAdd, 'years');
    const formatted = nextDate.format('YYYY');
    return <Option key={`select-year-${formatted}`} value={formatted}>{formatted}</Option>;
  });
  const newValue = (year) => {
    const numberYear = moment.utc(year, 'YYYY').year();
    const newMoment = value.clone().set({
      'year': numberYear
    });
    return newMoment;
  };
  return (
    <Select style={{width: 200}}
      defaultValue={value.format('YYYY')}
      onSelect={(year) => {
        onChange(newValue(year));
      }}>
      {options}
    </Select>
  );
};
SelectYear.propTypes = {
  value: PropTypes.object.isRequired, // moment
  onChange: PropTypes.func.isRequired
};


const SelectMonth = ({value, onChange}) => {
  const numberOfMonth = 12; // It is like ant.d
  const options = [...Array(numberOfMonth).keys()].map(i => {
    const nextDate = value.clone().set({'month': i});
    const formatted = nextDate.format('MMM');
    return <Option key={`select-year-${formatted}`} value={formatted}>{formatted}</Option>;
  });
  const newValue = (month) => {
    const numberMonth = moment.utc(month, 'MMM').month();
    const newMoment = value.clone().set({
      'month': numberMonth
    });
    return newMoment;
  };
  return (
    <Select style={{width: 200}}
      defaultValue={value.format('MMM')}
      onSelect={value => {
        onChange(newValue(value));
      }}>
      {options}
    </Select>
  );
};

SelectMonth.propTypes = {
  value: PropTypes.object.isRequired, // moment
  onChange: PropTypes.func.isRequired
};

const MonthNavigator = ({value, onChange}) => {
  return (
    <div>
      <Button icon={<LeftOutlined/>} onClick={() => onChange(value.clone().subtract(1, 'month'))}/>
      <Button icon={<RightOutlined/>} onClick={() => onChange(value.clone().add(1, 'month'))}/>
      <p>{value.format('MMM YYYY')}</p>
    </div>
  );
};
MonthNavigator.propTypes = {
  value: PropTypes.object.isRequired, // moment
  onChange: PropTypes.func.isRequired
};


const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;
const MonthCalendar = (props) => {
  const isDisabled = (item, date) => (item && item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(date));
  const findData = (date) => props.days.find(day => day.date.isSame(moment(date).utc(), 'day'));

  return (
    <div id="tk_MonthCalendar">
      <Calendar
        headerRender={({value, onChange}) => {
          return (
            <div id="tk_MonthCalendar_Head">
              <MonthNavigator value={value} onChange={onChange} />
              <div>
                <SelectMonth value={value} onChange={onChange} />
                <SelectYear value={value} onChange={onChange} />
              </div>
            </div>);
        }}
        disabledDate={moment => {
          const day = findData(moment);
          return isDisabled(day, moment);
        }}
        dateCellRender={moment => {
          const day = findData(moment);
          const className = props.warningCardPredicate && props.warningCardPredicate(moment, day && day.data) ?
            'tk_CardWeekCalendar_Body_With_Warn' : '';
          return (
            <div className={className}>
              <Button
                shape="circle"
                disabled={isDisabled(day, moment)}
                icon={<PlusOutlined/>}
                onClick={(e) => {
                  props.onClickAddTask && props.onClickAddTask(e, moment);
                  e.stopPropagation();
                }}/>
              {day && day.data && props.dateCellRender(day.data, day.date, day.disabled)}
            </div>
          );
        }}
      />
    </div>
  );
};

MonthCalendar.propTypes = {
  dateCellRender: PropTypes.func.isRequired, //(data, date, disabled) => node
  disabledWeekEnd: PropTypes.bool,
  days: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  onClickAddTask: PropTypes.func, // (event, moment) => void
  warningCardPredicate: PropTypes.func // (date, day) => bool
};

export default MonthCalendar;
