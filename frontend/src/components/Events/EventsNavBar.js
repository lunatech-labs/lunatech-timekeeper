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

import React, {useContext} from 'react';
import {Tabs} from 'antd';
import './EventsList.less';
import EventsList from './EventsList';
import {UserContext} from '../../context/UserContext';

const EventsNavBar = () => {

  const {TabPane} = Tabs;
  const {currentUser} = useContext(UserContext);
  return (
    <div>
      <Tabs defaultActiveKey="tk_companyEvent">
        <TabPane tab="Company Events" key="tk_companyEvent"><EventsList endPoint={'/api/events-template'}/></TabPane>
        <TabPane tab="Personal Events" key="tk_personnalEvent"><EventsList
          endPoint={'/api/user-events?userId=' + currentUser.id}/></TabPane>
        <TabPane tab="Users Events" key="tk_userEvents"><EventsList endPoint={'/api/user-events'}/></TabPane>
      </Tabs>
    </div>
  );
};
export default EventsNavBar;
