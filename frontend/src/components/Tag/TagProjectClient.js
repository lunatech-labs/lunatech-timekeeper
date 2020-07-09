import React from 'react';
import {Tag} from 'antd';
import './TagProjectClient.less';
import {PropTypes} from 'prop-types';


const TagProjectClient = ({client}) => {
  var stringToColour = function(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    var colour = '#';
    for (var j = 0; j < 3; j++) {
      var value = (hash >> (j * 8)) & 0xFF;
      colour += ('00' + value.toString(16)).substr(-2);
    }
    return colour;
  };

  return (
    <React.Fragment>{client ? <Tag color={stringToColour(client.name)}>{client.name}</Tag> : <span className="tk_Project_Client_Tag">No client</span>}</React.Fragment>
  );
};

TagProjectClient.propTypes = {
  client: PropTypes.shape({
    name: PropTypes.string
  })
};

export default TagProjectClient;
