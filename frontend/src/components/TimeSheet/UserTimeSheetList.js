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
import PropTypes from 'prop-types';
import ProjectCard from './ProjectCard';

const UserTimeSheetList = (props) => {
    const publicProject = props.timeSheets.filter(item => item.project.publicAccess);
    const privateProject = props.timeSheets.filter(item => !item.project.publicAccess);
    return (
        <React.Fragment>
            <ProjectCard project={privateProject} title="My Private Projects"/>
            <ProjectCard project={publicProject} title="My Public Projects"/>
        </React.Fragment>
    );
};

UserTimeSheetList.propTypes = {
  timeSheets: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      project: PropTypes.shape({
        name: PropTypes.string,
        publicAccess: PropTypes.bool,
        client: PropTypes.object
      }),
      defaultIsBillable: PropTypes.bool.isRequired,
      expirationDate: PropTypes.string,
      maxDuration: PropTypes.number,
      durationUnit: PropTypes.string,
      leftOver: PropTypes.number
    })).isRequired
};

export default UserTimeSheetList;