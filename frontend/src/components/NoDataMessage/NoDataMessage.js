import React  from 'react';
import './NoDataMessage.less';
import PropTypes from 'prop-types';

const NoDataMessage = ({message} ) => {
  return (
    <p className="tk_NoDataMessage">
      {message}
    </p>
  );
};

NoDataMessage.propTypes = {
  message: PropTypes.string.isRequired
};

export default NoDataMessage;