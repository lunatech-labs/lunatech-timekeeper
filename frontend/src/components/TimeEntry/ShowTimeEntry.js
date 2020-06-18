import React from 'react';
import PropTypes from "prop-types";
import {EditOutlined, CopyOutlined, DeleteOutlined}  from "@ant-design/icons/es/icons";
import './TimeEntry.less';

const ShowTimeEntry = ({entry}) => {
  return (
    <div className="tk_TaskInfo">
      <p>{entry.comment}</p>
      <div>
        <div>
          <p>{entry.project.name}</p>
          <p>4:00</p>
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