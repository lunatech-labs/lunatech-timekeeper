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
    return <Option key={`select-year-${formatted}`} value={nextDate.format('YYYY-MM-DD')}>{formatted}</Option>;
  });
  return (
    <Select style={{width: 200}} onSelect={(value) => {
      onChange(moment.utc(value, 'YYYY-MM-DD'));
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
    const toAdd = i - 6;
    const nextDate = value.clone().add(toAdd, 'month');
    const formatted = nextDate.format('MMM');
    return <Option key={`select-year-${formatted}`} value={nextDate.format('YYYY-MM-DD')}>{formatted}</Option>;
  });
  return (
    <Select style={{width: 200}} onSelect={value => {
      onChange(moment.utc(value, 'YYYY-MM-DD'));
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
      <Button icon={<LeftOutlined/>}
        onClick={() => onChange(value.clone().subtract(1, 'month'))}/>
      {value.format('MMM YYYY')}

      <Button icon={<RightOutlined/>}
        onClick={() => onChange(value.clone().add(1, 'month'))}/>
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
    <Calendar
      headerRender={({value, onChange}) => {
        return (<div>
          <SelectMonth value={value} onChange={onChange}/>
          <SelectYear value={value} onChange={onChange}/>
          <MonthNavigator value={value} onChange={onChange}/>
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
