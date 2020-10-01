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
import './EventMemberTag.less';
import PropTypes from 'prop-types';
import {Alert, Spin} from 'antd';
import {useTimeKeeperAPI} from '../../utils/services';
import TkUserAvatar from '../Users/TkUserAvatar';

const EventMemberTag = ({ member }) => {
  const usersResponse = useTimeKeeperAPI('/api/users');

  if (usersResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading list of projects</p>
        </Spin>
      </React.Fragment>
    );
  }

  if (usersResponse.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the list of projects'
          type='error'
          description='Unable to fetch the list of Projects from the server'
        />
      </React.Fragment>
    );
  }
  const user = usersResponse.data.filter(user => user.id === member.userId);
  return (
    <div className="tk_EventMember_Display">
      <TkUserAvatar name={user[0].name} picture={user[0].picture}/>
      <p>{user[0].name}</p>
    </div>
  );
};

EventMemberTag.propTypes = {
  member: PropTypes.shape({
    name: PropTypes.string.isRequired,
    picture: PropTypes.string,
    userId: PropTypes.number.isRequired
  })
};

export default EventMemberTag;