import React  from 'react';
import './CardMember.less';
import PropTypes from 'prop-types';
const CardMember = ( {children} ) => {

  return (
    <div className="tk_CardMember">
      {children}
    </div>
  );
};

CardMember.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired
};

export default CardMember;