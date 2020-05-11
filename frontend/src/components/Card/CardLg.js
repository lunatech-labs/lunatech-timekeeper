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
  children: PropTypes.arrayOf(PropTypes.element)
};

export default CardLg;