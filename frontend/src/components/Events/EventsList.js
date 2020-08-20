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

import React, {useState} from 'react';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, Card, List, Spin, Dropdown, Button, Menu, AutoComplete} from 'antd';
import CalendarOutlined from '@ant-design/icons/lib/icons/CalendarOutlined';
import EventMemberTag from './EventMemberTag';
import './EventsList.less';
import moment from 'moment';
import EventMembersPictures from './EventMembersPictures';
import EllipsisOutlined from '@ant-design/icons/lib/icons/EllipsisOutlined';
import CopyOutlined from '@ant-design/icons/lib/icons/CopyOutlined';
import {useKeycloak} from '@react-keycloak/web';
import EditFilled from '@ant-design/icons/lib/icons/EditFilled';
import _ from 'lodash';
import Pluralize from '../Pluralize/Pluralize';
import Input from 'antd/lib/input';
import SearchOutlined from '@ant-design/icons/lib/icons/SearchOutlined';
import DownOutlined from '@ant-design/icons/lib/icons/DownOutlined';

const EventsList = () => {
  const [keycloak] = useKeycloak();
  const isAdmin = keycloak.hasRealmRole('admin');
  const eventsResponse = useTimeKeeperAPI('/api/events');

  const [filterText, setFilterText] = useState('All');
  const [searchValue, setSearchValue] = useState('');

  const onSearch = searchText => setSearchValue(searchText);

  if (eventsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading list of projects</p>
        </Spin>
      </React.Fragment>
    );
  }

  if (eventsResponse.error) {
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

  const getEventsFiltered = () =>  eventsOrdered.filter(event => moment(event.startDateTime).format('MMMM') === filterText ||
        moment(event.endDateTime).format('MMMM') === filterText ||
        moment().month(filterText).isBetween(moment(event.startDateTime), moment(event.endDateTime)));

  // Sort events by startDateTime order (DESC) && filter with searchValue
  const eventsOrdered = _.orderBy(eventsResponse.data.filter(event => event.name.toLowerCase().includes(searchValue.toLowerCase())), (userEvent) => {
    return moment(userEvent.startDateTime).utc();
  },'desc');

  // Filter events by month
  const eventsFilter = () => {
    switch (filterText) {
      case 'All':
        return eventsOrdered;
      default:
        return getEventsFiltered();
    }
  };

  const menu = (item) => {
    return (
      <List
        id={'tk_EventMembers'}
        dataSource={item.attendees}
        renderItem={member => (
          <List.Item>
            <EventMemberTag member={member}/>
          </List.Item>
        )}
      />
    );
  };

  const dropdownCardAction = (item, isAdmin) => (
    <Menu>
      {isAdmin &&
            <Menu.Item key="edit">
              <a href={`/events/${item.id}/edit`}><EditFilled/>Edit</a>
            </Menu.Item>}
      <Menu.Item key="copy">
        <a href={'#'}><CopyOutlined />Copy</a>
      </Menu.Item>
    </Menu>
  );

  const displayMembersButton = (item) => {
    if(item.attendees.length === 0){
      return <Button className="tk_Link_People" type="link">{item.attendees.length}{' people'}</Button>;
    }
    return(
      <Dropdown overlay={menu(item)} key="members">
        <Button className="tk_Link_People" type="link" onClick={e => e.preventDefault()}>{item.attendees.length}{' people'}</Button>
      </Dropdown>
    );
  };

  const formatDateEvent = (date) => {
    return moment(date, 'YYYY-MM-DD-HH:mm:ss.SSS\'Z\'').utc().format('LLL');
  };

  const filterMenu = (
    <Menu className="tk_Filter_Month" onClick={({key}) => setFilterText(key)}>
      <Menu.Item key="All">
                All
      </Menu.Item>
      {
        moment.months().map(month => {
          return (
            <Menu.Item key={month}>
              {month}
            </Menu.Item>
          );
        })
      }
    </Menu>
  );

  const filterComponent = (
    <React.Fragment>
      <p>Month :</p>
      <Dropdown overlay={filterMenu}>
        <Button type="link" className="ant-dropdown-link">
          {filterText} <DownOutlined/>
        </Button>
      </Dropdown>
    </React.Fragment>
  );

  return(
    <div>
      <div className="tk_SubHeader">
        <p><Pluralize label="event" size={eventsFilter().length}/></p>
        <div className="tk_SubHeader_RightPart">
          <div className="tk_SubHeader_Filters">{filterComponent}</div>
          <div className="tk_Search_Input">
            <AutoComplete onSearch={onSearch}>
              <Input data-cy="searchClientBox" size="large" placeholder="Search in events..." allowClear  prefix={<SearchOutlined />} />
            </AutoComplete>
          </div>
        </div>
      </div>
      <List
        id="tk_List"
        grid={{gutter: 32, column: 3}}
        dataSource={eventsFilter()}
        renderItem={item => (
          <List.Item key={`event-list-${item.id}`}>
            <Card
              id="tk_CardEvent"
              bordered={false}
              title={item.name}
              extra={[
                <Dropdown key={`ant-dropdown-${item.id}`} overlay={dropdownCardAction(item, isAdmin)}>
                  <a className="ant-dropdown-link" onClick={e => e.preventDefault()}><EllipsisOutlined /></a>
                </Dropdown>,
              ]}
            >
              <div className="tk_EventCard_Body">
                <p className="tk_CardEvent_Desc">{item.description}</p>
                <div className="tk_CardEvent_Bottom">
                  <div className="tk_CardEvent_Date">
                    <CalendarOutlined />
                    <p>{formatDateEvent(item.startDateTime)}<br />{formatDateEvent(item.endDateTime)}</p>
                  </div>
                  <div className="tk_CardEvent_People">
                    <div>
                      <EventMembersPictures key={`event-member-picture-${item.id}`} membersIds={item.attendees.map(user => user.userId)} />
                    </div>
                    {displayMembersButton(item)}
                  </div>
                </div>
              </div>
            </Card>
          </List.Item>
        )}
      />
    </div>
  );
};
export default EventsList;