import React from 'react';
import PropTypes from 'prop-types';
import './ShowTimeEntry.less';
import {Button} from 'antd';
import Tooltip from 'antd/lib/tooltip';
import {ClockCircleOutlined, DeleteOutlined, CopyOutlined, EditOutlined} from '@ant-design/icons';

const moment = require('moment');

const ShowTimeEntry = ({entry, onClickEdit}) => {

  const start = moment(entry.startDateTime).utc();
  const end = moment(entry.endDateTime).utc();

  const duration = moment.duration(end.diff(start));
  const date = start.clone();
  date.set({
    hour: duration.asHours()
  });

  return (
    <div className="tk_TaskInfoGen">
      <div className="tk_TaskInfo">
        <p className="tk_TaskInfoTop">{entry.comment}</p>
        <div className="tk_TaskInfoMiddle">
          <div>
            <p>{entry.project ? entry.project.name : ''}</p>
            <p><ClockCircleOutlined />{date.format('hh:mm')}</p>
          </div>
          <div>
            <Tooltip title="Edit" key="edit">
              <Button onClick={onClickEdit} type="link" size="small" shape="circle" icon={<EditOutlined />}/>
            </Tooltip>
            <Tooltip title="Copy" key="copy">
              <Button type="link" size="small" shape="circle" icon={<CopyOutlined />}/>
            </Tooltip>
            <Tooltip title="Delete" key="delete">
              <Button type="link" size="small" shape="circle" icon={<DeleteOutlined />}/>
            </Tooltip>
          </div>
        </div>
      </div>
      <div className="tk_TaskInfoBottom">
      </div>
    </div>
  );
};

ShowTimeEntry.propTypes = {
  entry: PropTypes.shape({
    comment: PropTypes.string.isRequired,
    project: PropTypes.shape({
      name: PropTypes.string.isRequired,
    }).isRequired,
    startDateTime: PropTypes.string.isRequired,
    endDateTime: PropTypes.string.isRequired,
  }),
  onClickEdit: PropTypes.func.isRequired // () => set the mode to 'edit', the mode can be 'view', 'add' or 'edit'
};

export default ShowTimeEntry;