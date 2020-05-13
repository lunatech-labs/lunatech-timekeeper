import React  from 'react';
import './CardMember.less';
import PropTypes from 'prop-types';
const CardMember = ( {children} ) => {

  return (
    <div className="tk_Card_Member">
      {children}
    </div>
  );
};

CardMember.propTypes = {
  children: PropTypes.arrayOf(PropTypes.element)
};

export default CardMember;