/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
