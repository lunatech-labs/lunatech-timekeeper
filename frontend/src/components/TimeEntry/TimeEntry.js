import React from 'react';
import './TimeEntry.less';
import {Badge} from 'antd';
import {FieldTimeOutlined} from '@ant-design/icons';
import PropTypes from 'prop-types';

const moment = require('moment');
const TimeEntry = ({entry}) => {

  const computeSize = (nbHours) => {
    const minimumSize = 75;

    return minimumSize + (nbHours - 1) * 50;
  };

  const start = moment(entry.startDateTime).utc();
  const end = moment(entry.endDateTime).utc();
  const duration = moment.duration(end.diff(start));
  const date = start.clone();
  date.set({
    hour: duration.asHours()
  });
  const hours = duration.asHours();
  const size = computeSize(hours);
  return (
    <div className="tk_TaskCard" style={{height: `${size}px`}}
      key={`badge-entry-${start && start.format('yyyy-mm-dd-hh-mm')}`}>
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