import React from 'react';
import PropTypes from 'prop-types';
import './Pluralize.less';

const Pluralize = ({label,size}) => {
  if(size === null || size === 0){
    return (
      <span className="tk_pluralize">No {label}</span>
    );
  }
  if(size == 1) {
    return (
      <span className="tk_pluralize">1 {label}</span>
    );
  }
  return (
    <span className="tk_pluralize">{size} {label}s</span>
  );
};

Pluralize.propTypes = {
  label: PropTypes.string.isRequired,
  size: PropTypes.number.isRequired,
};

export default Pluralize;