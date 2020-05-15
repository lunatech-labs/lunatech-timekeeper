import React from 'react';
import './TagMember.less';
import {Tag} from 'antd';
import PropTypes from 'prop-types';

const TagMember = ({isManager}) => {
  return(
    <Tag id="tk_Tag" className={isManager ? 'tk_Tag_Gold' : ''}>
      {isManager ? 'TeamLeader' : 'Member'}
    </Tag>
  );
};

TagMember.propTypes = {
  isManager: PropTypes.bool.isRequired
};
export default TagMember;