import React from 'react';
import {Button, Calendar, Select, ConfigProvider} from 'antd';
import PropTypes from 'prop-types';
import {LeftOutlined, PlusOutlined, RightOutlined} from '@ant-design/icons';
import moment from 'moment';
import './MonthCalendar.less';
import {isWeekEnd} from '../../utils/momentUtils';
import en_GB from 'antd/lib/locale-provider/en_GB';
import 'moment/locale/en-gb';  // important!

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
      value={value.format('YYYY')}
      onSelect={(year) => onChange(newValue(year))}>
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
      value={value.format('MMM')}
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


const MonthCalendar = (props) => {
  const isDisabled = (item, date) => (item && item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(date));
  const findData = (date) => props.days.find(day => day.date.isSame(moment(date).utc(), 'day'));

  return (
    <div id="tk_MonthCalendar">
        <ConfigProvider locale={en_GB}>
      <Calendar
        headerRender={({value, onChange}) => {
          const onChangeCustom = (date) => {
            onChange(date);
            props.onPanelChange(date);
          };
          return (
            <div id="tk_MonthCalendar_Head">
              <MonthNavigator value={value} onChange={onChangeCustom} />
              <div>
                <SelectMonth value={value} onChange={onChangeCustom} />
                <SelectYear value={value} onChange={onChangeCustom} />
              </div>
            </div>);
        }}
        disabledDate={moment => {
          const day = findData(moment);
          return isDisabled(day, moment);
        }}
        dateCellRender={moment => {
          const day = findData(moment);
          const className = !(props.disabledWeekEnd && isWeekEnd(moment)) && (props.warningCardPredicate && props.warningCardPredicate(moment, day && day.data)) ?
            'tk_CardMonthCalendar_Body_With_Warn' : '';
          return (
            <div className={className}>
              <Button
                shape="circle"
                disabled={isDisabled(day, moment)}
                icon={<PlusOutlined/>}
                onClick={(e) => {
                  props.onClickButton && props.onClickButton(e, moment);
                  e.stopPropagation();
                }}/>
              {day && day.data && props.dateCellRender(day.data, day.date, day.disabled)}
            </div>
          );
        }}
      />
        </ConfigProvider>
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
  onClickButton: PropTypes.func, // (event, moment) => void
  warningCardPredicate: PropTypes.func, // (date, day) => bool
  onPanelChange: PropTypes.func // (date) => void
};

export default MonthCalendar;
