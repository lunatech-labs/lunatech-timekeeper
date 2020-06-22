import React  from 'react';
import './CardXs.less';
import PropTypes from 'prop-types';
const CardXs = ({children} ) => {

  return (
    <div className="tk_CardMember">
      {children}
    </div>
  );
};

CardXs.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired
};

export default CardXs;