import React, {useState} from 'react';
import {Alert, Avatar, Button, Card, Collapse, Divider, Dropdown, List, Menu, Spin} from 'antd';
import logo from '../../img/logo_timekeeper_homepage.png';
import {useTimeKeeperAPI} from '../../utils/services';
import EditFilled from '@ant-design/icons/lib/icons/EditFilled';
import UserOutlined from '@ant-design/icons/lib/icons/UserOutlined';
import LockFilled from '@ant-design/icons/lib/icons/LockFilled';
import UnlockOutlined from '@ant-design/icons/lib/icons/UnlockOutlined';

import Tooltip from 'antd/lib/tooltip';

import Space from 'antd/lib/space';
import Meta from 'antd/lib/card/Meta';

import './ProjectList.less';
import ProjectMemberTag from './ProjectMemberTag';
import EyeFilled from '@ant-design/icons/lib/icons/EyeFilled';
import TitleSection from '../Title/TitleSection';
import DownOutlined from '@ant-design/icons/lib/icons/DownOutlined';
import PropTypes from 'prop-types';

const {Panel} = Collapse;

const ProjectList = () => {

  const [filterText, setFilterText] = useState('All');

  const [groupBy, setGroupBy] = useState('All');

  const projectsResponse = useTimeKeeperAPI('/api/projects');

  const projects = () => projectsResponse.data.sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));

  const projectsFilter = () => {
    switch (filterText) {
      case 'All':
        return projects();
      case 'Private':
        return projects().filter(project => !project.publicAccess);
      case 'Public':
        return projects().filter(project => project.publicAccess);
      default:
        return projects();
    }
  };

  const groupByClient = () => {
    const map = new Map();
    projectsFilter().forEach(project => {
      const key = (project.client && project.client.id) || null;
      const collection = map.get(key);
      if (!collection) {
        map.set(key, [project]);
      } else {
        collection.push(project);
      }
    });
    const data = [];
    map.forEach(value => {
      const client = value[0].client;
      const newData = {
        client: client,
        projects: value
      };
      data.push(newData);
    });
    return data.sort((a, b) => {
      if (!a.client) {
        return 1;
      } else if (!b.client) {
        return -1;
      }
      return a.client.name.localeCompare(b.client.name);
    });
  };

  const groupByMenu = (
    <Menu onClick={({key}) => setGroupBy(key)}>
      <Menu.Item key="All">
        All
      </Menu.Item>
      <Menu.Item key="Client">
        Client
      </Menu.Item>
    </Menu>
  );

  const groupByComponent = (
    <React.Fragment>
      <p>Group by :</p>
      <Dropdown overlay={groupByMenu}>
        <a className="ant-dropdown-link">
          {groupBy} <DownOutlined/>
        </a>
      </Dropdown>
    </React.Fragment>
  );

  const filterMenu = (
    <Menu onClick={({key}) => setFilterText(key)}>
      <Menu.Item key="All">
        All
      </Menu.Item>
      <Menu.Item key="Private">
        Private
      </Menu.Item>
      <Menu.Item key="Public">
        Public
      </Menu.Item>
    </Menu>
  );

  const filterComponent = (
    <React.Fragment>
      <p>Filter by :</p>
      <Dropdown overlay={filterMenu}>
        <a className="ant-dropdown-link">
          {filterText} <DownOutlined/>
        </a>
      </Dropdown>
    </React.Fragment>
  );

  if (projectsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading list of projects</p>
        </Spin>
      </React.Fragment>
    );
  }

  if (projectsResponse.error) {
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

  const memberComparator = (m1, m2) => m2.manager - m1.manager;

  const DataList = ({data}) => <List
    id="tk_List"
    grid={{gutter: 32, column: 3}}
    dataSource={data}
    renderItem={item => (
      <List.Item key={item.id}>
        <Card
          id="tk_Card_Sm"
          bordered={false}
          title={
            <Space size={'middle'}>
              <Avatar src={logo} shape={'square'} size="large"/>
              <div className="tk_Card_Sm_Header">
                <div>
                  <p>{item.name}</p>
                  <p>{item.client ? item.client.name : 'No client'}</p>
                </div>
                <p>{item.publicAccess ? <UnlockOutlined/> :
                  <LockFilled/>}<span>{item.publicAccess ? ' Public' : ' Private project'}</span></p>
              </div>
            </Space>
          }
          extra={[
            <Tooltip title="View" key="view">
              <Button type="link" size="small" ghost shape="circle" icon={<EyeFilled/>} href={`/projects/${item.id}`}/>
            </Tooltip>,
            <Tooltip title="Edit" key="edit">
              <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>}
                href={`/projects/${item.id}/edit`}/>
            </Tooltip>
          ]}
          actions={[ item.users.length === 0 ? <Panel id="tk_ProjectNoCollapse" header={<Space size="small"><UserOutlined/>{item.users.length}{item.users.length <= 1 ? 'member' : 'members'}</Space>}/> :
            <Collapse bordered={false} expandIconPosition={'right'} key="projects">
              <Panel header={<Space size="small"><UserOutlined/>{item.users.length}{item.users.length <= 1 ? 'member' : 'members'}</Space>} key="members">
                <List
                  className={'tk_Project_MemberList'}
                  dataSource={item.users.sort(((a, b) => memberComparator(a, b)))}
                  renderItem={member => (
                    <List.Item><ProjectMemberTag member={member}/></List.Item>
                  )}
                />
              </Panel>
            </Collapse>
          ]}
        >
          <Meta
            description={item.description}
          />
        </Card>
      </List.Item>
    )}
  />;
  DataList.propTypes = {
    data: PropTypes.array
  };

  const projectsFiltered = projectsFilter();

  return (
    <React.Fragment>
      <div className="tk_SubHeader">
        <p>{projectsFiltered.length} project(s)
          | {Array.from(new Set(projectsFiltered.filter(project => !!project.client).map((project) => project.client.id))).length} client(s)</p>
        <div className="tk_SubHeader_RightPart">
          <div className="tk_SubHeader_Filters">{filterComponent}</div>
          <div className="tk_SubHeader_Filters">{groupByComponent}</div>
        </div>
      </div>

      {groupBy === 'All' ?
        <DataList data={projectsFiltered}/> :
        groupByClient().map(data =>
          <div key={`projects-of-clients-${(data.client && data.client.id) || 0}`}>
            <TitleSection title={(data.client && data.client.name) || 'No client'}/>
            <Divider/>
            <DataList data={data.projects}/>
          </div>
        )
      }
    </React.Fragment>
  );
};
export default ProjectList;
