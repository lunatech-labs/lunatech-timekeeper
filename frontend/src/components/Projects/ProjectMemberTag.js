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
import './ProjectMemberTag.less';
import PropTypes from 'prop-types';
import TagMember from '../Tag/TagMember';
import CardXs from '../Card/CardXs';
import TkUserAvatar from '../Users/TkUserAvatar';

const ProjectMemberTag = ({ member }) => {
  return (
    <CardXs>
      <div>
        <TkUserAvatar picture={member.picture} name={member.name}/>
        <p>{member.name}</p>
      </div>
      <TagMember isManager={member.manager} />
    </CardXs>
  );
};

ProjectMemberTag.propTypes = {
  member: PropTypes.shape({
    name: PropTypes.string.isRequired,
    picture: PropTypes.string,
    manager: PropTypes.bool.isRequired
  })
};

export default ProjectMemberTag;