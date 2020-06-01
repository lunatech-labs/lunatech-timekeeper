import React  from 'react';
import './CardLg.less';
import PropTypes from 'prop-types';
const CardLg = ( {children} ) => {

  return (
    <div className="tk_Card_Lg">
      {children}
    </div>
  );
};

CardLg.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired
};

export default CardLg;