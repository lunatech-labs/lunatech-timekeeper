import React, {useState} from 'react';
import PropTypes from "prop-types";
import {EditOutlined, CopyOutlined, DeleteOutlined}  from "@ant-design/icons/es/icons";
import './TimeEntry.less';

const moment = require('moment');

const computeNumberOfHours = (start, end) => {
  const duration = moment.duration(end.diff(start));
  const date = start.clone();
  date.set({
    hour: duration.asHours()
  });
  return duration.asHours();
};

const ShowTimeEntry = ({entry}) => {

  console.log("test", entry);

  const start = moment(entry.startDateTime).utc();
  const end = moment(entry.endDateTime).utc();
  const hours = computeNumberOfHours(start, end);

  return (
    <div className="tk_TaskInfo">
      <p>{entry.comment}</p>
      <div>
        <div>
          <p>{entry.project ? entry.project.name : 'String vide'}</p>
          <p>{hours}</p>
        </div>
        <div>
          <EditOutlined />
          <CopyOutlined />
          <DeleteOutlined />
        </div>
      </div>
    </div>
  )
}

ShowTimeEntry.propTypes = {
  entry: PropTypes.shape({
    comment: PropTypes.string.isRequired,
    project: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
    startDateTime: PropTypes.string.isRequired,
    endDateTime: PropTypes.string.isRequired,
  })
};

export default ShowTimeEntry;