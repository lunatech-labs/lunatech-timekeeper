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
import PropTypes from 'prop-types';
import {Alert, Button, Form, Input, Spin} from 'antd';
import {useTimeKeeperAPI, useTimeKeeperAPIDelete} from '../../utils/services';
import '../Modal/ModalGeneral.less';
import NoDataMessage from '../NoDataMessage/NoDataMessage';
import ShowTimeEntry from './ShowTimeEntry';
import AddEntryForm from './AddEntryForm';
import EditEntryForm from './EditEntryForm';
import UserEventCard from '../UserEvent/UserEventCard';
import {totalHoursPerDay} from '../../utils/momentUtils';
import {getMaximumHoursPerDay} from '../../utils/configUtils';

const {TextArea} = Input;

const TimeEntryForm = ({entries, userEvents, currentDay, form, onSuccess, onCancel, mode, setMode, selectedEntryId}) => {
  const setAddMode = () => setMode('add');
  const setEditMode = () => setMode('edit');
  const [entry, setEntry] = useState();

  useEffect(() => {
    if(selectedEntryId) {
      const localEntry = entries[0].find(e => e.id === selectedEntryId);
      if (localEntry) {
        setEntry(localEntry);
      }
    }
  },[entries, selectedEntryId]);

  const timeSheets = useTimeKeeperAPI('/api/my/' + currentDay.year() + '?weekNumber=' + currentDay.isoWeek(), (form => form));
  if (timeSheets.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <Form
            labelCol={{span: 4}}
            wrapperCol={{span: 14}}
            layout="horizontal"
          >
            <Form.Item label="Name" name="name">
              <Input placeholder="Loading data from server..."/>
            </Form.Item>
            <Form.Item label="Description" name="description">
              <TextArea
                rows={4}
                placeholder="Loading data from server..."
              />
            </Form.Item>
          </Form>
        </Spin>

      </React.Fragment>
    );
  }
  if (timeSheets.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the data'
          type='error'
        />
      </React.Fragment>
    );
  }
  const UserEventsAndEntries = ({entries, userEvents, date}) => {
    const displayUserEventsAndEntries = (entries, userEvents, date) => {
      if(entries && userEvents){
        return(
          <div>
            <div>
              <Entries entries={entries} />
            </div>
            <div>
              <UserEvents userEvents={userEvents} date={date} />
            </div>
          </div>
        );
      }
      return (
        <div>
          <UserEvents userEvents={userEvents} date={date} />
        </div>
      );
    };
    return displayUserEventsAndEntries(entries, userEvents, date);
  };
  const Entries = ({entries}) => {
    const [urlDelete, setDeleteUrl] = useState('');
    const callDeleteEntry = useTimeKeeperAPIDelete(urlDelete);

    // Todo: Avoid console errors and find better way to use Hooks and call API delete
    useEffect(() => {
      return () => {
        callDeleteEntry.run().then(onSuccess && onSuccess());
      };
    }, [urlDelete]);

    const onDelete = (timeSheetId, timeEntryId) => {
      const url = `/api/timeSheet/${timeSheetId}/timeEntry/${timeEntryId}/delete`;
      setDeleteUrl(url);
    };

    const showTimeEntries = (entries) => {
      return entries.map(entry => <ShowTimeEntry key={entry.id} entry={entry} onClickEdit={()=>{
        setEntry(entry);
        setEditMode();
      }} onClickDelete={ () => {
        const timeSheetId = (timeSheets.data.sheets.entries) ? timeSheets.data.sheets.filter(timeSheet => timeSheet.entries.find(item => entry.id === item.id)).map(sheet => sheet.id)[0] : null;
        onDelete(timeSheetId, entry.id);
      }}/>);
    };
    return (
      <div className="tk_TaskInfoList">
        {showTimeEntries(entries)}
      </div>
    );
  };
  const UserEvents = ({userEvents, date}) => {
    const showUserEvents = (userEvents) => {
      return userEvents.map(userEvent => {
        return [...Array(userEvent.eventUserDaysResponse.length).keys()]
          .filter(i => date.format('YYYY-MM-DD') === userEvent.eventUserDaysResponse[i].date)
          .map(i => {
            return <UserEventCard event={userEvent.eventUserDaysResponse[i]} key={`tk_UserEventCard_${userEvent.eventUserDaysResponse[i].startDateTime}`}/>;
          });
      });
    };
    return showUserEvents(userEvents);
  };
  // Returns the number of hours for a day
  const amountOfHoursPerDay = (entriesArray, userEvents, date) => {
    return totalHoursPerDay(userEvents, date, entriesArray[0]);
  };
  return (
    <div className="tk_ModalGen">
      <div className="tk_ModalTop">
        <div className="tk_ModalTopHead">
          <div>
            <p>{currentDay.format('ddd')}<br/><span>{currentDay.format('DD')}</span></p>
            <h1>Day information</h1>
          </div>
          { (mode === 'view' || mode === 'edit') && amountOfHoursPerDay(entries, userEvents, currentDay) < getMaximumHoursPerDay() ?
            <Button type="link" onClick={() => setMode && setAddMode()}>Add task</Button> : ''}
        </div>
        <div className="tk_ModalTopBody">
          {entries.length === 0 && userEvents.length === 0 ?
            <NoDataMessage message='No task for this day, there is still time to add one.'/> :
            (mode === 'edit' ?
              <Entries entries={selectedEntryId ? [entry] : entries[0]}/> :
              <UserEventsAndEntries entries={entries[0]} userEvents={userEvents} date={currentDay} />)
          }
        </div>
      </div>
      {mode === 'add' &&
          <AddEntryForm date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess}
            onCancel={onCancel} numberOfHoursForDay={amountOfHoursPerDay(entries, userEvents, currentDay)}/>}
      {mode === 'edit' && entry &&
          <EditEntryForm date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess}
            onCancel={onCancel} entry={entry} numberOfHoursForDay={amountOfHoursPerDay(entries, userEvents, currentDay)}/>}
    </div>
  );
};
TimeEntryForm.propTypes = {
  currentDay: PropTypes.object.isRequired,
  form: PropTypes.object,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
  mode: PropTypes.string, // can be 'view', 'add' or 'edit'
  setMode: PropTypes.func,
  entries: PropTypes.array,
  selectedEntryId: PropTypes.number,
  userEvents: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number,
      date: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
      eventUserDaysResponse: PropTypes.arrayOf(
        PropTypes.shape({
          name: PropTypes.string,
          description: PropTypes.string,
          startDateTime: PropTypes.string,
          endDateTime: PropTypes.string,
          date: PropTypes.string
        })
      ),
      eventType: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string,
      duration: PropTypes.string
    })
  )
};
export default TimeEntryForm;