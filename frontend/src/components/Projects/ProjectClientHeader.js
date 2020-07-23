/*
 * Copyright 2020 Lunatech S.A.S
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
import {Avatar, Divider} from 'antd';
import TagProjectClient from '../Tag/TagProjectClient';
import PropTypes from 'prop-types';
import './ProjectClientHeader.less';
import _ from 'lodash';

const getLogoURL = (item) => {
    if(item && item.name) {
        let cleanSeed = _.snakeCase(item.name);
        return 'https://picsum.photos/seed/' + cleanSeed + '/40';
    }else{
        return 'https://picsum.photos/40';
    }
};

const ProjectClientHeader = ({project}) => {
  return (
    <div className='tk_ProjectClientHeader'>
      <Avatar src={getLogoURL(project)} shape={'square'} size="large"/>
      <p>{project.name}</p>
      <Divider type="vertical"/>
      <TagProjectClient client={project.client}/>
    </div>
  );
};

ProjectClientHeader.propTypes = {
  project: PropTypes.shape({
    name: PropTypes.string,
    client: PropTypes.object
  })
};


export default ProjectClientHeader;
