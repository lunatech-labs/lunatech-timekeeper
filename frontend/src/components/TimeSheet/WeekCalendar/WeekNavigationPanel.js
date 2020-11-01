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

import {Button, Select} from 'antd';
import PropTypes from 'prop-types';
import moment from 'moment';
import React, {useEffect, useState} from 'react';
import _ from 'lodash';
import {LeftOutlined, RightOutlined} from '@ant-design/icons';
import {useHistory} from 'react-router-dom';

import {renderRange, renderRangeWithYear, weekRangeOfDate} from '../../../utils/momentUtils';

const numberOfWeek = 15; // It's the number of weeks where we can navigate

const computeWeekRanges = (selectedDay) => {
  const weekRanges = weekRangeOfDate(selectedDay, numberOfWeek);
  return {
    weekNumber: selectedDay.isoWeek(),
    weekRange: weekRanges,
    weekRangeMap: _.keyBy(weekRangeOfDate(moment().utc().startOf('week'), 1).concat(weekRanges), 'id'),
    weekNumberCurrent: moment().utc().startOf('week').week()
  };
};

const WeekNavigationPanel = (props) => {
  const {firstDay, onDateChange, onPanelChange } = props;
  const weekSelected = firstDay.isoWeek();
  const setWeekSelected = (weekNumber) => onDateChange(moment().isoWeek(weekNumber).startOf('week'));
  const [weekRanges, setWeekRanges] = useState(computeWeekRanges(firstDay));

  const history = useHistory();

  useEffect(() => {
    const weekRange = _.get(weekRanges.weekRangeMap, weekSelected);
    if (weekRange && onPanelChange) {
      const {id, start, end} = weekRange;
      onPanelChange(id, start, end);
      if (weekRanges.weekNumber !== id) {
        setWeekRanges(computeWeekRanges(start));
        history.push('?year=2020&weekNumber=' + id);
      }
    }
  }, [weekSelected, onPanelChange, weekRanges, history]);

  const WeekNavigator = () => {
    const weekRangeIds = weekRanges.weekRange.map(weekRange => weekRange.id);
    const weekRange = _.get(weekRanges.weekRangeMap, weekSelected);
    const {start, end} = weekRange;
    const disableLeft = !weekRangeIds.includes(weekSelected - 1);
    const disableRight = !weekRangeIds.includes(weekSelected + 1);
    return (
      <div data-cy='weekNavigator'>
        <Button data-cy='btnWeekPrevious' data-cy-week={weekSelected - 1} icon={<LeftOutlined/>} disabled={disableLeft} onClick={() => setWeekSelected(weekSelected - 1)}/>
        <Button data-cy='btnWeekNext' data-cy-week={weekSelected + 1} icon={<RightOutlined/>} disabled={disableRight} onClick={() => setWeekSelected(weekSelected + 1)}/>
        <p>{renderRangeWithYear(start, end)}</p>
      </div>
    );
  };

  const WeekNavigatorSelect = () => {
    return <Select
      data-cy='selectWeekNavigator'
      onChange={id => setWeekSelected(id)}
      defaultValue={0}
      value={weekSelected}
      dropdownRender={menu => (
        <div>
          <div className={'tk_Select_CurrentWeek'}
            onClick={() => setWeekSelected(weekRanges.weekNumberCurrent)}>
                        Current Week
          </div>
          {menu}
        </div>)}>
      {weekRanges.weekRange.map(({id, start, end}) => {
        return (
          <Select.Option className={`${start.isSame(moment(), 'week') ? 'tk_CurrentWeekSelect' : ''}`}
            key={`date-range-${id}`} value={id} disabled={id === weekSelected}>
            {renderRange(start, end)}
          </Select.Option>
        );
      })}
    </Select>;
  };

  return (
    <div id="tk_WeekCalendar_Head">
      <WeekNavigator/>
      <div>
        <WeekNavigatorSelect/>
      </div>
    </div>
  );
};

WeekNavigationPanel.propTypes = {
  firstDay: PropTypes.instanceOf(moment).isRequired,
  onDateChange: PropTypes.func,
  onPanelChange: PropTypes.func
};

export default WeekNavigationPanel;
