import moment from 'moment';
import _ from 'lodash';
import {isPublicHoliday, isWeekEnd} from '../../utils/momentUtils';
import PropTypes from 'prop-types';


export const IsDisabled = (dateAsMoment, timeEntriesData, disabledWeekEnd, publicHolidays) => {
  const userTimeEntry = UserTimeEntryData(dateAsMoment, timeEntriesData);
  return userTimeEntry.disabled ? true :
    (isWeekEnd(dateAsMoment) && disabledWeekEnd) || isPublicHoliday(dateAsMoment, publicHolidays);
};
IsDisabled.PropTypes = {
  dateAsMoment: PropTypes.instanceOf(moment),
  timeEntriesData: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
  disabledWeekEnd: PropTypes.bool,
  publicHolidays: PropTypes.object
};

export const UserTimeEntryData = (date, timeEntriesData) => {
  const data = timeEntriesData.find(entry => entry.date.isSame(moment(date).utc(), 'day'));
  return _.isUndefined(data) ? {} : data;
};
UserTimeEntryData.PropTypes = {
  date: PropTypes.instanceOf(moment),
  timeEntriesData: PropTypes.arrayOf(
    PropTypes.shape({
      date: PropTypes.object,
      disabled: PropTypes.bool,
      data: PropTypes.any
    })
  ).isRequired,
};

export const UserEventEntryData = (userEvents, dateAsMoment) => {
  const data = userEvents.map(userEvent => userEvent.eventUserDaysResponse).flat()
    .filter(eventEntry => dateAsMoment.format('YYYY-MM-DD') === eventEntry.date);
  return _.isUndefined(data) ? [] : data;
};
UserEventEntryData.PropTypes = {
  dateAsMoment: PropTypes.instanceOf(moment),
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

export const IsDayWithoutAnyEntries = (currentDate, userTimeEntry, userEventEntry) => {
  const today = moment();
  const isCurrentDateBeforeToday = currentDate.isBefore(today , 'day');
  const isUserTimeEntryEmpty = _.isEmpty(userTimeEntry);
  const isUserEventEntryEmpty = _.isEmpty(userEventEntry);
  return(isCurrentDateBeforeToday && isUserTimeEntryEmpty && isUserEventEntryEmpty);
};
IsDayWithoutAnyEntries.PropTypes = {
  currentDate: PropTypes.instanceOf(moment),
  userEventEntry: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string,
      description: PropTypes.string,
      startDateTime: PropTypes.string,
      endDateTime: PropTypes.string,
      date: PropTypes.string
    })
  ).isRequired,
  userTimeEntry: PropTypes.shape({
    date: PropTypes.object,
    disabled: PropTypes.bool,
    data: PropTypes.any
  }).isRequired
};