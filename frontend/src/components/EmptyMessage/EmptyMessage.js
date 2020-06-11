import React  from 'react';
import './EmptyMessage.less';
import PropTypes from 'prop-types';

const EmptyMessage = ( {children} ) => {
  return (
    <p className="tk_EmptyMessage">
      {children}
    </p>
  );
};

EmptyMessage.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired
};

export default EmptyMessage;