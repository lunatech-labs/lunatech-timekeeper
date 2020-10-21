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

import React, {useContext, useState} from 'react';
import {useTimeKeeperAPI} from '../../utils/services';
import {Alert, AutoComplete, Button, Card, Dropdown, List, Menu, Spin} from 'antd';
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
import PropTypes from 'prop-types';
import {UserContext} from '../../context/UserContext';

const EventsList = ({endPoint}) => {
  const [keycloak] = useKeycloak();
  const isAdmin = keycloak.hasRealmRole('admin');
  const eventsResponse = useTimeKeeperAPI(endPoint);
  const {currentUser} = useContext(UserContext);

  const [filterDate, setFilterDate] = useState('All');
  const [filterCreator, setFilterCreator] = useState('Mine');
  const [searchValue, setSearchValue] = useState('');


  const onSearch = searchText => setSearchValue(searchText);

  const isPersonalEvent = (endPoint) => {
    return endPoint !== '/api/events-template' && endPoint !== '/api/user-events';
  };

  const isUserEvent = (endPoint) => {
    return endPoint === '/api/user-events';
  };

  if (eventsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading list of Company Events</p>
        </Spin>
      </React.Fragment>
    );
  }

  if (eventsResponse.error) {
    return (
      <React.Fragment>
        <Alert title='Server error' message='Failed to load the list of events' type='error'
          description='Unable to fetch the list of Events from the server'
        />
      </React.Fragment>
    );
  }

  const isCreator = (event) => {
    return event.creatorId === currentUser.id;
  };

  const getEventsFiltered = () => eventsOrdered.filter(event => {
    const startDateTime = moment(event.startDateTime);
    const endDateTime = moment(event.endDateTime);
    return (
      startDateTime.format('MMMM') === filterDate ||
        endDateTime.format('MMMM') === filterDate ||
        moment().month(filterDate).isBetween(startDateTime, endDateTime)
    );
  });

  const mineToShow = (event) => (isUserEvent(endPoint) && filterCreator === 'Mine') ? isCreator(event) : true;

  // Sort events by startDateTime order (DESC) && filter with searchValue
  const eventsOrdered = _.orderBy(eventsResponse.data.filter(event => event.name.toLowerCase().includes(searchValue.toLowerCase())), (userEvent) => {
    return moment(userEvent.startDateTime).utc();
  }, 'desc').filter(mineToShow);

  // Filter events by month
  const eventsFilter = () => {
    switch (filterDate) {
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

  const dropdownCardAction = (item, isAdmin, userEvent) => (
    <Menu>
      {isAdmin &&
      <Menu.Item key="edit">
        {/*The edition link is disabled for userevents cards*/}
        { userEvent ? <a href='#' className='tk_Link_Disabled'><EditFilled/>Edit</a> : <a href={`/events/${item.id}/edit`}><EditFilled/>Edit</a>}
      </Menu.Item>}
      <Menu.Item key="copy">
        <a href={'#'} className='tk_Link_Disabled'><CopyOutlined/>Copy</a>
      </Menu.Item>
    </Menu>
  );

  const displayMembersButton = (item) => {
    if (item.attendees.length === 0) {
      return <Button className="tk_Link_People" type="link">{item.attendees.length}{' people'}</Button>;
    }
    return (
      <Dropdown overlay={menu(item)} key="members">
        <Button className="tk_Link_People" type="link"
          onClick={e => e.preventDefault()}>{item.attendees.length}{' people'}</Button>
      </Dropdown>
    );
  };

  const formatDateEvent = (date) => {
    return moment(date, 'YYYY-MM-DD-HH:mm:ss.SSS\'Z\'').utc().format('LLL');
  };

  const filterDateMenu = (
    <Menu className="tk_Filter_Month" onClick={({key}) => setFilterDate(key)}>
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

  const filterCreatorMenu = (
    <Menu className="tk_Filter_Creator" onClick={({key}) => setFilterCreator(key)}>
      <Menu.Item key="Mine">
          Mine
      </Menu.Item>
      <Menu.Item key="All">
          All
      </Menu.Item>
    </Menu>
  );

  const filterComponent = (
    <React.Fragment>
      {
        isUserEvent(endPoint) ?
          <React.Fragment>
            <p>Creator :</p>
            <Dropdown overlay={filterCreatorMenu}>
              <Button type="link" className="ant-dropdown-link">
                {filterCreator} <DownOutlined/>
              </Button>
            </Dropdown>
          </React.Fragment>
          : ''
      }
      <p>Month :</p>
      <Dropdown overlay={filterDateMenu}>
        <Button type="link" className="ant-dropdown-link">
          {filterDate} <DownOutlined/>
        </Button>
      </Dropdown>
    </React.Fragment>
  );

  return (
    <div>
      <div className="tk_SubHeader">
        <p><Pluralize label="event" size={eventsFilter().length}/></p>
        <div className="tk_SubHeader_RightPart">
          <div className="tk_SubHeader_Filters">{filterComponent}</div>
          <div className="tk_Search_Input">
            <AutoComplete onSearch={onSearch}>
              <Input data-cy="searchClientBox" size="large" placeholder="Search in events..." allowClear
                prefix={<SearchOutlined/>}/>
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
                <Dropdown key={`ant-dropdown-${item.id}`} overlay={dropdownCardAction(item, isAdmin, isPersonalEvent(endPoint))}>
                  <a className="ant-dropdown-link" onClick={e => e.preventDefault()}><EllipsisOutlined/></a>
                </Dropdown>,
              ]}
            >
              <div className="tk_EventCard_Body">
                <p className="tk_CardEvent_Desc">{item.description}</p>
                <div className="tk_CardEvent_Bottom">
                  <div className="tk_CardEvent_Date">
                    <CalendarOutlined/>
                    <p>{formatDateEvent(item.startDateTime)}<br/>{formatDateEvent(item.endDateTime)}</p>
                  </div>
                  <div className="tk_CardEvent_People">
                    <div>
                      <EventMembersPictures key={`event-member-picture-${item.id}`}
                        membersIds={item.attendees.map(user => user.userId)}/>
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

EventsList.propTypes = {
  endPoint: PropTypes.string.isRequired
};

export default EventsList;
