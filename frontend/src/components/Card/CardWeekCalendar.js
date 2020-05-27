import React from 'react';
import PropTypes from 'prop-types';
import './CardWeekCalendar.less'
const CardWeekCalendar = ( {children, disabled} ) => {

  return (
    <div className="tk_CardWeekCalendar" disabled={disabled}>
      {children}
    </div>
  );
};

CardWeekCalendar.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired,
  disabled: PropTypes.bool
};



export default CardWeekCalendar;