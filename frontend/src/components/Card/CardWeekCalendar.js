import React from 'react';
import PropTypes from 'prop-types';
import './CardWeekCalendar.less';
const CardWeekCalendar = ( {children, onClick, onMouseOver, onMouseLeave} ) => {

  return (
    <div className="tk_CardWeekCalendar" onClick={onClick} onMouseOver={onMouseOver} onMouseLeave={onMouseLeave}>
      {children}
    </div>
  );
};

CardWeekCalendar.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired,
  disabled: PropTypes.bool,
  onMouseOver: PropTypes.func,
  onMouseLeave: PropTypes.func,
  onClick: PropTypes.func
};

export default CardWeekCalendar;