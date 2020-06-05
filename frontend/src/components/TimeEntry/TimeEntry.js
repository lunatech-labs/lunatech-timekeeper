import React from 'react';
import './TimeEntry.less';
import {Badge} from 'antd';
import {FieldTimeOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';

const moment = require('moment');
const TimeEntry = ({entry}) => {

  const computeSize = (dateTime) => {
    const minimumSize = 75;

    return minimumSize + (dateTime.hours() - 1) * 50;
  };

//FIXME BUG 1 heure de décalage
  const date = (moment(Date.parse(entry.endDateTime) - (Date.parse(entry.startDateTime))));
  console.log('date', date);
  const size = computeSize(date);
  return (
    <div className="tk_TaskCard" style={{height: `${size}px`}}
         key={`badge-entry-${date && date.format('yyyy-mm-dd-hh-mm')}`}>
      <div>
        <Badge
          status={(entry && entry.comment) ? 'success' : 'error'}
          text={(entry && entry.comment) ? `${entry.comment}` : 'Nothing to render'}
        />
        <p>{(entry && entry.project && entry.project.name) ? entry.project.name : ''}</p>
      </div>
      <p><FieldTimeOutlined/>{date.format('hh:mm')}</p>
    </div>
  );
};

TimeEntry.propTypes = {
  entry: {
    name: PropTypes.string.isRequired,
    project: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
    dateTime: PropTypes.object.isRequired,
  }
};

export default TimeEntry;