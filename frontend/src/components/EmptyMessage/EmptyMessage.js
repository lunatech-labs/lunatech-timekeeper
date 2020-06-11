import React  from 'react';
import './EmptyMessage.less';
import PropTypes from 'prop-types';

const EmptyMessage = ( {message} ) => {
  return (
    <p className="tk_EmptyMessage">
      {message}
    </p>
  );
};

EmptyMessage.propTypes = {
  message: PropTypes.string.isRequired
};

export default EmptyMessage;