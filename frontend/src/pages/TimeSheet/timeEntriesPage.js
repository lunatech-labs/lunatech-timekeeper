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
import WeekCalendar from '../../components/TimeSheet/WeekCalendar';
import TimeEntry from '../../components/TimeEntry/TimeEntry';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Badge, Form, Modal} from 'antd';
import TimeEntryForm from '../../components/TimeEntry/TimeEntryForm';
import UserTimeSheetList from '../../components/TimeSheet/UserTimeSheetList';
import MonthCalendar from '../../components/TimeSheet/MonthCalendar';
import CalendarSelectionMode from '../../components/TimeSheet/CalendarSelectionMode';
import {getIsoMonth, isNotPublicHoliday, isNotWeekEnd} from '../../utils/momentUtils';

//https://stackoverflow.com/questions/14446511/most-efficient-method-to-groupby-on-an-array-of-objects/34890276#34890276
const groupBy = function (xs, key) {
  return xs.reduce(function (rv, x) {
    (rv[key(x)] = rv[key(x)] || []).push(x);
    return rv;
  }, {});
};

const computeData = (timeSheets) => Object.entries(groupBy(timeSheets.flatMap(({entries, project}) => entries.map(x => ({
  ...x,
  project
}))), entry => moment(entry.startDateTime).format('YYYY-MM-DD'))).map(([key, value]) => {
  return ({
    data: value,
    date: moment(key),
    disabled: false
  });
});

const TimeEntriesPage = () => {
  let history = useHistory();
  let location = useLocation();
  const [calendarMode, setCalendarMode] = useState('week');
  const firstDayOfCurrentWeek = moment().utc().startOf('week');
  const today = () => firstDayOfCurrentWeek.clone();
  const [prefixWeekUrl, setPrefixWeekUrl] = useState(() => {
    if (location.search) {
      let searchParams = queryString.parse(location.search);
      if (searchParams.weekNumber && searchParams.year) {
        return searchParams.year + '?weekNumber='+searchParams.weekNumber;
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
  const days = computeData(timeSheets);

  const publicHolidays = calendarMode === 'week' ?
    (weekData.data && !weekData.loading ? weekData.data.publicHolidays : []) : (monthData.data && !monthData.loading ? monthData.data.publicHolidays : []);

  const userEvents = calendarMode === 'week' ?
    (weekData.data && !weekData.loading ? weekData.data.userEvents : []) : (monthData.data && !monthData.loading ? monthData.data.userEvents : []);

  const datas = {
    firstDayOfWeek: weekData.data ? moment.utc(weekData.data.firstDayOfWeek) : today(),
    days: days
  };

  const entriesOfSelectedDay = days.filter(day => day.date.format('YYYY-MM-DD') === taskMoment.format('YYYY-MM-DD'));

  const resetForm = () => form.resetFields();
  const openModal = () => setVisibleEntryModal(true);
  const closeModal = () => setVisibleEntryModal(false);


  // A day without entries in the past should be displayed with "warn" design
  const hasWarnNoEntryInPastDay = (date, day) => {
    return moment().subtract('1', 'days').isAfter(date) && !day;
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

  const removeDuplicatesElementAndSort = (array) => {
    const set = new Set(array);
    const arraySorted = [...set].sort((a, b) => a.localeCompare(b));
    return arraySorted;
  };

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
            firstDay={datas.firstDayOfWeek}
            disabledWeekEnd={true}
            hiddenButtons={false}
            onPanelChange={(id, start) => setPrefixWeekUrl(start.year() + '?weekNumber=' + start.isoWeek())}
            onClickButton={onClickAddTask}
            onClickCard={onClickCard}
            dateCellRender={(data, date) => {
              return (
                <div>
                  {data.filter(data => !!data).map(entry => {
                    return (
                      <TimeEntry key={entry.id} entry={entry}
                        onClick={e => onClickEntryCard(e, date, entry.id)}/>
                    );
                  })}
                </div>
              );
            }}
            days={datas.days}
            publicHolidays={publicHolidays}
            userEvents={userEvents}
            warningCardPredicate={hasWarnNoEntryInPastDay}
          /> :
          <MonthCalendar
            onClickButton={onClickAddTask}
            days={datas.days}
            dateCellRender={(data, date) => {
              const projectsName = data.filter(data => !!data).map(item => item.project.name);
              return removeDuplicatesElementAndSort(projectsName).map(item =>
                <div key={`badge-entry-project-${item}-${date.format('YYYY-MM-DD')}`}>
                  <Badge status='success' text={item}/>
                </div>);
            }}
            disabledWeekEnd={true}
            warningCardPredicate={hasWarnNoEntryInPastDay}
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