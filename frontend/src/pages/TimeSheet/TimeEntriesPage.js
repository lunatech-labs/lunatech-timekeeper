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

import React, {useEffect, useState} from 'react';
import {useHistory} from 'react-router-dom';
import {useLocation} from 'react-router';
import queryString from 'query-string';
import moment from 'moment';
import MainPage from '../MainPage/MainPage';
import WeekCalendar from '../../components/TimeSheet/WeekCalendar/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';
import UserTimeSheetList from '../../components/TimeSheet/UserTimeSheetList';
import MonthCalendar from '../../components/TimeSheet/MonthCalendar/MonthCalendar';
import CalendarSelectionMode from '../../components/TimeSheet/CalendarSelectionMode';
import {getIsoMonth, isNotPublicHoliday, isNotWeekEnd} from '../../utils/momentUtils';
import {groupBy} from '../../utils/jsFunctionUtils';

const computeData = (timeSheets) => Object.entries(
  groupBy(timeSheets.flatMap(({entries, project}) => entries.map(x => ({...x, project}))), entry => entry.startDateTime))
  .map(([date, timeEntry]) => {
    return ({ data: timeEntry, date: moment(date), disabled: false});
  });

const TimeEntriesPage = () => {
  const history = useHistory();
  const location = useLocation();
  const [calendarMode, setCalendarMode] = useState('week');
  const firstDayOfCurrentWeek = moment().utc().startOf('week');
  const [contextDate, setContextDate] = useState(firstDayOfCurrentWeek);
  const [prefixWeekUrl, setPrefixWeekUrl] = useState(() => {
    if (location.search) {
      let searchParams = queryString.parse(location.search);
      if (searchParams.weekNumber && searchParams.year) {
        return searchParams.year + '?weekNumber=' + searchParams.weekNumber;
      }
      if (searchParams.weekNumber) {
        return '2020?weekNumber='+searchParams.weekNumber;
      }
    }
    const tmpDate = firstDayOfCurrentWeek.clone();
    const weekNumber = tmpDate.isoWeek();
    history.push('?year=' + tmpDate.year() + '&weekNumber=' + weekNumber);
    return tmpDate.year() + '?weekNumber=' + weekNumber;
  });
  const [prefixMonthUrl, setPrefixMonthUrl] = useState(() => {
    const tmpDate = firstDayOfCurrentWeek.clone();
    return `${tmpDate.year()}/month?monthNumber=${getIsoMonth(tmpDate)}`;
  });
  const [visibleEntryModal, setVisibleEntryModal] = useState(false);
  const [mode, setMode] = useState('view'); //Can be 'view', 'add' or 'edit'
  const [taskMoment, setTaskMoment] = useState(moment().utc());
  const [form] = Form.useForm();
  const [selectedEntryId, setSelectedEntryId] = useState();

  const weekData = useTimeKeeperAPI('/api/my/' + prefixWeekUrl);

  useEffect(
    () => {
      if(weekData.loading){
        return ;
      }
      weekData.run();
    },
    /* eslint-disable react-hooks/exhaustive-deps */
    [prefixWeekUrl, weekData.run]);
  /* eslint-enable react-hooks/exhaustive-deps */

  const monthData = useTimeKeeperAPI(`/api/my/${prefixMonthUrl}`);
  useEffect(
    () => {
      if(monthData.loading){
        return;
      }
      monthData.run();
    },
    /* eslint-disable react-hooks/exhaustive-deps */
    [prefixMonthUrl, monthData.run]);
  /* eslint-enable react-hooks/exhaustive-deps */

  if (weekData.error || monthData.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the list of TimeSheets for this user'
          type='error'
          description='check that the authenticated User has role [user] on Quarkus'
        />
      </React.Fragment>
    );
  }

  const timeSheets = calendarMode === 'week' ?
    (weekData.data && !weekData.loading ? weekData.data.sheets : []) : (monthData.data && !monthData.loading ? monthData.data.sheets : []);
  const timeEntriesData = computeData(timeSheets);

  const publicHolidays = calendarMode === 'week' ?
    (weekData.data && !weekData.loading ? weekData.data.publicHolidays : []) : (monthData.data && !monthData.loading ? monthData.data.publicHolidays : []);

  const userEvents = calendarMode === 'week' ?
    (weekData.data && !weekData.loading ? weekData.data.userEvents : []) : (monthData.data && !monthData.loading ? monthData.data.userEvents : []);

  const entriesOfSelectedDay = timeEntriesData.filter(day => day.date.format('YYYY-MM-DD') === taskMoment.format('YYYY-MM-DD'));

  const resetForm = () => form.resetFields();
  const openModal = () => setVisibleEntryModal(true);
  const closeModal = () => setVisibleEntryModal(false);

  const convertDateTimeToDate = (dateTime) => moment(moment(dateTime).format('YYYY-MM-DD'), 'YYYY-MM-DD');

  const getEventsOnDate = (date) => userEvents.filter(userEvent =>
    date.isBetween(
      convertDateTimeToDate(userEvent.startDateTime).subtract(1, 'second'),
      convertDateTimeToDate(userEvent.endDateTime).add(1, 'day')
    )
  );

  // A day without entries in the past should be displayed with "warn" design
  const hasWarnNoEntryInPastDay = (date, day) => {
    const hasEventOnThisDate = getEventsOnDate(date).length > 0;
    return moment().subtract('1', 'days').isAfter(date) && !day && !hasEventOnThisDate;
  };

  const onClickAddTask = (e, m) => {
    setTaskMoment(m);
    setAddMode();
    openModal();
  };

  const onClickCard = (e, m) => {
    if(isNotWeekEnd(m) && isNotPublicHoliday(m, publicHolidays)){
      setTaskMoment(m);
      setViewMode();
      openModal();
    }
  };

  const onClickEntryCard = (e, m, selectedEntryId) => {
    setSelectedEntryId(selectedEntryId);
    setTaskMoment(m);
    setEditMode();
    openModal();
    e.stopPropagation();
  };

  /* const removeDuplicatesElementAndSort = (array) => {
    const set = new Set(array);
    const arraySorted = [...set].sort((a, b) => a.localeCompare(b));
    return arraySorted;
  };
*/
  const setViewMode = () => setMode('view');
  const setAddMode = () => setMode('add');
  const setEditMode = () => setMode('edit');

  const timeEntryForm = () => {
    if (selectedEntryId && mode === 'edit') {
      return <TimeEntryForm selectedEntryId={selectedEntryId} setMode={setMode}
        entries={entriesOfSelectedDay.map(entries => entries.data)} currentDay={taskMoment}
        userEvents={userEvents} form={form} mode={mode}
        onSuccess={() => {
          closeModal();
          weekData.run();
          monthData.run();
          setViewMode();
        }} onCancel={() => setViewMode()}
      />;
    }
    return <TimeEntryForm setMode={setMode} entries={entriesOfSelectedDay.map(entries => entries.data)}
      userEvents={userEvents} currentDay={taskMoment} form={form} mode={mode}
      onSuccess={() => {
        closeModal();
        weekData.run();
        monthData.run();
        setViewMode();
      }} onCancel={() => setViewMode()}
    />;
  };

  return (
    <MainPage title="Time entries">
      <Modal
        visible={visibleEntryModal}
        onCancel={closeModal}
        destroyOnClose={true}
        afterClose={resetForm}
        width={'37.5%'}
        footer={null}
      >
        {timeEntryForm()}
      </Modal>
      <UserTimeSheetList timeSheets={timeSheets}/>
      <CalendarSelectionMode onChange={e => setCalendarMode(e.target.value)}/>
      {
        calendarMode === 'week' ?
          <WeekCalendar
            onDateChange={setContextDate}
            firstDay={contextDate}
            disabledWeekEnd={true}
            hiddenButtons={false}
            onPanelChange={(id, start) => setPrefixWeekUrl(start.year() + '?weekNumber=' + start.isoWeek())}
            onClickButton={onClickAddTask}
            onClickCard={onClickCard}
            dateCellRender={(data, date) => {
              return (
                <div>
                  {data.map(entry => {
                    console.log('Entry: ' + JSON.stringify(entry));
                    return (
                      <TimeEntry key={entry.id} entry={entry}
                        onClick={e => onClickEntryCard(e, date, entry.id)}/>
                    );
                  })}
                </div>
              );
            }}
            timeEntriesData={timeEntriesData}
            publicHolidays={publicHolidays}
            userEvents={userEvents}
            warningCardPredicate={hasWarnNoEntryInPastDay}
          /> :
          <MonthCalendar
            contextDate={contextDate}
            onDateChange={(date) => setContextDate(date.utc().startOf('week'))}
            onClickPlusButton={onClickAddTask}
            timeEntriesData={timeEntriesData}
            disabledWeekEnd={true}
            onPanelChange={(date) => {
              setPrefixMonthUrl(`${date.year()}/month?monthNumber=${getIsoMonth(date)}`);
            }}
            publicHolidays={publicHolidays}
            userEvents={userEvents}
          />
      }
    </MainPage>
  );
};
export default TimeEntriesPage;