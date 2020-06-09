import React from 'react';
import {Tag} from 'antd';
import './TagProjectClient.less';
import {PropTypes} from 'prop-types';
const TagProjectClient = ({client}) => {
  return (
    <React.Fragment>{client ? <Tag color="blue">{client.name}</Tag> : <span className="tk_Project_Client_Tag">No client</span>}</React.Fragment>
  );
};

TagProjectClient.propTypes = {
  client: {
    name: PropTypes.string
  }
};

export default TagProjectClient;
