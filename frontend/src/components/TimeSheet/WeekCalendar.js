import React, {useEffect, useState} from 'react';
import { useHistory } from 'react-router-dom'
import {Button, Select, Tag} from 'antd';
import CardWeekCalendar from '../Card/CardWeekCalendar';
import {LeftOutlined, PlusOutlined, RightOutlined, CheckOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';
import './WeekCalendar.less';
import {isWeekEnd, renderRange, renderRangeWithYear, weekRangeOfDate, computeHoursForADay} from '../../utils/momentUtils';
import moment from 'moment';

const numberOfWeek = 15; // It's the number of weeks where we can navigate

const weekRangeOfDateToMap = (weekRanges) => {
  const map = new Map();
  weekRanges.forEach((item) => map.set(item.id, item));
  return map;
};
const computeWeekRanges = (selectedDay) => {
  const weekRanges = weekRangeOfDate(selectedDay, numberOfWeek);
  return {
    weekNumber: selectedDay.isoWeek(),
    weekRange: weekRanges,
    weekRangeMap: weekRangeOfDateToMap(weekRanges)
  };
};

const WeekCalendar = (props) => {
  const [showButton, setShowButton] = useState(-1);
  const [weekSelected, setWeekSelected] = useState(props.firstDay.isoWeek());
  const [weekRanges, setWeekRanges] = useState(computeWeekRanges(props.firstDay));

  const {onPanelChange} = props;
  const history = useHistory();

  useEffect(() => {
    const weekRange = weekRanges.weekRangeMap.get(weekSelected);
    if (weekRange && onPanelChange) {
      const {id, start, end} = weekRange;
      onPanelChange(id, start, end);
      if (weekRanges.weekNumber !== id) {
        setWeekRanges(computeWeekRanges(start));
        history.push('?weekNumber=' + id)
      }
    }
  }, [weekSelected, onPanelChange, weekRanges, history]);


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
  const isDisabled = (item, numberOfHours) => (item.day && item.day.disabled) || (props.disabledWeekEnd && isWeekEnd(item.date)) || numberOfHours >= 8;

  const WeekNavigator = () => {
    const weekRangeIds = weekRanges.weekRange.map(weekRange => weekRange.id);
    const weekRange = weekRanges.weekRangeMap.get(weekSelected);
    const {start, end} = weekRange;
    const disableLeft = !weekRangeIds.includes(weekSelected - 1);
    const disableRight = !weekRangeIds.includes(weekSelected + 1);
    return (
      <div>
        <Button data-cy='btnWeekPrevious' data-cy-week={weekSelected - 1} icon={<LeftOutlined/>} disabled={disableLeft} onClick={() => setWeekSelected(weekSelected - 1)}/>
        <Button data-cy='btnWeekNext' data-cy-week={weekSelected + 1} icon={<RightOutlined/>} disabled={disableRight} onClick={() => setWeekSelected(weekSelected + 1)}/>
        <p>{renderRangeWithYear(start, end)}</p>
      </div>
    );
  };

  const WeekNavigatorSelect = () =>
    <Select onChange={id => setWeekSelected(id)} defaultValue={0} value={weekSelected}>
      {weekRanges.weekRange.map(({id, start, end}) => {
        return (
          <Select.Option className={`${start.isSame(moment(), 'week') ? 'tk_CurrentWeekSelect' : ''}`}
            key={`date-range-${id}`} value={id} disabled={id === weekSelected}>
            {renderRange(start, end)}
          </Select.Option>
        );
      })}
    </Select>;

  return (
    <div id="tk_WeekCalendar">
      <div id="tk_WeekCalendar_Head">
        <WeekNavigator/>
        <div>
          <WeekNavigatorSelect/>
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

          const isToday = (day) => {
            return moment().isSame(day, 'day');
          };

          return (
            <div className="tk_WeekCalendar_Day" key={`day-card-${index}`}>
              <p>{item.date.format(headerDateFormat)}</p>
              <CardWeekCalendar
                onClick={(e) => props.onClickCard && props.onClickCard(e, item.date)}
                onMouseOver={() => setShowButton(index)}
                onMouseLeave={() => setShowButton(-1)}>
                <div className="tk_CardWeekCalendar_Head">
                  <p className={isToday(moment(item.date)) ? 'tk_CurrentDay' : ''}>{item.date.format(dateFormat)}</p>
                  {((props.hiddenButtons && showButton === index) || (!props.hiddenButtons)) &&
                  <Button
                    shape="circle"
                    disabled={isDisabled(item, item.day ? computeHoursForADay(item.day.data) : 0)}
                    icon={<PlusOutlined/>}
                    onClick={(e) => {
                      props.onClickButton && props.onClickButton(e, item.date);
                      e.stopPropagation();
                    }}/> }
                </div>
                <div className={props.warningCardPredicate && props.warningCardPredicate(item.date, item.day) ?
                  'tk_CardWeekCalendar_Body tk_CardWeekCalendar_Body_With_Warn' : 'tk_CardWeekCalendar_Body'} disabled={isDisabled(item)}>
                  {renderDay()}
                  <div className='tk_CardWeekCalendar_Bottom'>
                    {item.day ? computeHoursForADay(item.day.data) < 8 ? '' : <Tag className="tk_Tag_Competed"><CheckOutlined /> Completed</Tag> : ''}
                  </div>
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
  onClickButton: PropTypes.func, // (event, moment) => void
  onPanelChange: PropTypes.func, // (id, start, end) => void
  onClickCard: PropTypes.func, // (event, moment) => void
  warningCardPredicate : PropTypes.func // (date, day) => bool
};

export default WeekCalendar;