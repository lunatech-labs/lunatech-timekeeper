import React from 'react';
import PropTypes from 'prop-types';
import './TitleSection.less';

const TitleSection = ({title}) => {
  return (
    <p className="tk_SectionTitle">
      {title}
    </p>
  );
};

TitleSection.propTypes = {
  title: PropTypes.string.isRequired,
};

export default TitleSection;