import React, {useState} from 'react';
import PropTypes from 'prop-types';
import {Alert, Button, Form, Input, Spin} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';
import '../Modal/ModalGeneral.less';
import NoDataMessage from '../NoDataMessage/NoDataMessage';
import ShowTimeEntry from './ShowTimeEntry';
import AddEntryForm from './AddEntryForm';
import EditEntryForm from './EditEntryForm';
import moment from 'moment';

const {TextArea} = Input;

const TimeEntryForm = ({entries, currentDay, form, onSuccess, onCancel, mode, setMode, entryId}) => {

  const setAddMode = () => setMode('add');
  const setEditMode = () => setMode('edit');
  const [entry, setEntry] = useState();
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
  const Entries = ({entries, entryId}) => {
    if(entryId) {
      const entriesFiltered = entries.map(entriesMap => entriesMap.filter(entry => entry.id === entryId));
      const showEntries = entriesFiltered.map(entry => <ShowTimeEntry key={entry[0].id} entry={entry[0]} onClickEdit={()=>{
        setEntry(entry[0]);
        setEditMode();
      }}/>);
      return (
        <div className="tk_TaskInfoList">
          {showEntries}
        </div>
      );
    }
    const showEntries = entries.map(
      entriesForDay => entriesForDay.map(entry => <ShowTimeEntry key={entry.id} entry={entry} onClickEdit={()=>{
        setEntry(entry);
        setEditMode();
      }}/>),
    );
    return (
      <div className="tk_TaskInfoList">
        {showEntries}
      </div>
    );
  };

  // Returns the number of hours for a day
  const amountOfHoursPerDay = (entriesArray) => {
    return entriesArray.map(entries => {
      const reducer = (accumulator, currentValue) => accumulator + currentValue;
      return entries.map( entry => {
        const start = moment(entry.startDateTime).utc();
        const end = moment(entry.endDateTime).utc();
        const duration = moment.duration(end.diff(start));
        return duration.asHours();
      }).reduce(reducer);
    });
  };
  if(mode === 'edit'){
    const entriesFiltered = entries.map(entriesMap => entriesMap.filter(entry => entry.id === entryId));
    const entry = entriesFiltered.map(entry => entry[0]);
    if(entry) {
      return (
          <div className="tk_ModalGen">
            <div className="tk_ModalTop">
              <div className="tk_ModalTopHead">
                <div>
                  <p>{currentDay.format('ddd')}<br/><span>{currentDay.format('DD')}</span></p>
                  <h1>Day information</h1>
                </div>
                { (mode === 'view' || mode === 'edit') && amountOfHoursPerDay(entries) < 8 ?
                    <Button type="link" onClick={() => setMode && setAddMode()}>Add task</Button> : ''}
              </div>
              <div className="tk_ModalTopBody">
                {entries.length === 0 ?
                    <NoDataMessage message='No task for this day, there is still time to add one.'/> :
                    (mode === 'edit' ? <Entries entries={entries} entryId={entryId}/> : <Entries entries={entries} />)
                }
              </div>
            </div>
            {mode === 'edit' && entry &&
            <EditEntryForm date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess}
                           onCancel={onCancel} entry={entry[0]}/>}
          </div>
      );
    }
  }
  return (
    <div className="tk_ModalGen">
      <div className="tk_ModalTop">
        <div className="tk_ModalTopHead">
          <div>
            <p>{currentDay.format('ddd')}<br/><span>{currentDay.format('DD')}</span></p>
            <h1>Day information</h1>
          </div>
          { (mode === 'view' || mode === 'edit') && amountOfHoursPerDay(entries) < 8 ?
            <Button type="link" onClick={() => setMode && setAddMode()}>Add task</Button> : ''}
        </div>
        <div className="tk_ModalTopBody">
          {entries.length === 0 ?
            <NoDataMessage message='No task for this day, there is still time to add one.'/> :
            (mode === 'edit' ? <Entries entries={entries} entryId={entryId}/> : <Entries entries={entries} />)
          }
        </div>
      </div>
      {mode === 'add' &&
          <AddEntryForm date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess}
            onCancel={onCancel}/>}
      {mode === 'edit' && entry &&
          <EditEntryForm date={currentDay} form={form} timeSheets={timeSheets.data.sheets} onSuccess={onSuccess}
            onCancel={onCancel} entry={entry}/>}
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
  entryId: PropTypes.number
};

export default TimeEntryForm;