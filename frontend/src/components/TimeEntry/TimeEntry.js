import React, { Component } from 'react';
import './TimeEntry.less';
import {Badge} from "antd";
import {FieldTimeOutlined} from "@ant-design/icons";
import PropTypes from 'prop-types';

const TimeEntry = ({entry}) => {

  const computeSize = (dateTime) => {
    const minimumSize = 90

    //return minimumSize + (dateTime.hours() -1) * 30

    //style={{height: `${size}px`}}

    return 45 * dateTime.hours()
  }

  const size = computeSize(entry.dateTime)

  return (
    <div className="PagoLand" key={`badge-entry-${entry.dateTime && entry.dateTime.format('yyyy-mm-dd-hh-mm')}`}>
      <Badge
        status={(entry && entry.name) ? 'success' : 'error'}
        text={(entry && entry.name) ? `${entry.name}` : 'Nothing to render'}
      />
      <p>{(entry && entry.project && entry.project.name) ? entry.project.name: ''}</p>
      <p><FieldTimeOutlined />{entry.dateTime.format('hh:mm')}</p>
    </div>
  );
}

TimeEntry.propTypes = {
  entry: {
    name:PropTypes.string.isRequired,
    project:PropTypes.shape({
      name:PropTypes.string.isRequired,
    }).isRequired,
    dateTime:PropTypes.object.isRequired,
  }
}

export default TimeEntry;