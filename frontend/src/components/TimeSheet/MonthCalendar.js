/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import {Button, Calendar, ConfigProvider, Select, Tag} from 'antd';
import PropTypes from 'prop-types';
import {CheckOutlined, InfoCircleOutlined, LeftOutlined, PlusOutlined, RightOutlined} from '@ant-design/icons';
import moment from 'moment';
import './MonthCalendar.less';
import {isPublicHoliday, isWeekEnd, totalHoursPerDay} from '../../utils/momentUtils';
import en_GB from 'antd/lib/locale-provider/en_GB';
import 'moment/locale/en-gb';
import _ from 'lodash'; // important!

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
    return value.clone().set({
      'year': numberYear
    });
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
    return value.clone().set({
      'month': numberMonth
    });
  };
  return (
    <Select style={{width: 200}}
      defaultValue={value.format('MMM')}
      value={value.format('MMM')}
      onSelect={v => {
        onChange(newValue(v));
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
  const publicHolidays = props.publicHolidays;

  const isDisabled = (dateAsMoment) => {
    if(_.isObjectLike(dateAsMoment)) {
      const associatedData =  findData(dateAsMoment);
      if(associatedData && associatedData.disabled){
        return true;
      }else{
        return (isWeekEnd(dateAsMoment) && props.disabledWeekEnd) || isPublicHoliday(dateAsMoment, publicHolidays);
      }
    }else{
      console.log('Invalid dateAsMoment, should be an Object of type Moment');
      return false;
    }
  };

  const findData = (date) => props.days.find(day => day.date.isSame(moment(date).utc(), 'day'));

  const MonthCardComponent = ({item}) => {

    let associatedData =  findData(item);
    const className = !(props.disabledWeekEnd && (isWeekEnd(item) || isPublicHoliday(item) ) ) && (props.warningCardPredicate && props.warningCardPredicate(item, associatedData && associatedData.data)) ?
      'tk_CardMonthCalendar_Body_With_Warn' : '';

    if(!isDisabled(item)){
      if(associatedData && associatedData.date && totalHoursPerDay(associatedData.data) >= 8) {
        return (
          <div className={className}>
            <Tag className="tk_Tag_Completed"><CheckOutlined /> Completed</Tag>
            {props.dateCellRender(associatedData.data, associatedData.date, associatedData.disabled)}
          </div>
        );
      }
      return <div className={className}>
        <Button
          shape="circle"
          icon={<PlusOutlined/>}
          onClick={(e) => {
            props.onClickButton && props.onClickButton(e, item);
            e.stopPropagation();
          }}>
        </Button>
        {associatedData && associatedData.data && props.dateCellRender(associatedData.data, associatedData.date, associatedData.disabled)}
      </div>;
    }

    if(isPublicHoliday(item)){
      return <Tag className="tk_Tag_Public_Holiday"><InfoCircleOutlined /> Public holiday</Tag>;
    }

    return <div className='tk_CardMonthCalendar_Body'/>;
  };
  MonthCardComponent.propTypes = {
    item: PropTypes.instanceOf(moment)
  };

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
          disabledDate={dateAsMoment => {
            return isDisabled(dateAsMoment);
          }}
          dateCellRender={dateAsMoment => {
            return (
              <MonthCardComponent item={dateAsMoment}/>
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
  onPanelChange: PropTypes.func, // (date) => void
  publicHolidays:  PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.string,
      localName: PropTypes.string,
      name: PropTypes.string,
      countryCode: PropTypes.string
    })
  )
};

export default MonthCalendar;
