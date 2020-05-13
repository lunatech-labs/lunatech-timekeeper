import React, {useState} from 'react';
import {Alert, Avatar, Button, Card, Collapse, List, Spin, Row, Col, Dropdown, Menu} from 'antd';
import logo from '../../img/logo_timekeeper_homepage.png';
import {useTimeKeeperAPI} from '../../utils/services';
import EditFilled from '@ant-design/icons/lib/icons/EditFilled';
import UserOutlined from '@ant-design/icons/lib/icons/UserOutlined';
import LockFilled from '@ant-design/icons/lib/icons/LockFilled';
import UnlockOutlined from '@ant-design/icons/lib/icons/UnlockOutlined';
import { DownOutlined } from '@ant-design/icons';

import Tooltip from 'antd/lib/tooltip';

import Space from 'antd/lib/space';
import Meta from 'antd/lib/card/Meta';

import './ProjectList.less';
import ProjectMemberTag from './ProjectMemberTag';
import EyeFilled from '@ant-design/icons/lib/icons/EyeFilled';

const { Panel } = Collapse;

const ProjectList = () => {

  const [filterText, setFilterText] = useState('All');

  const projectsResponse = useTimeKeeperAPI('/api/projects');

  const projects = () => projectsResponse.data.sort((a,b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));

  const projectsFilter = () => {
    switch (filterText) {
    case 'All':
      return projects();
    case 'Private':
      return projects().filter(project => !project.publicAccess);
    case 'Public':
      return projects().filter(project => project.publicAccess);
    }
  };

  const filterBy = ({ key }) => {
    setFilterText(key);
  };

  const filterMenu = (
    <Menu onClick={filterBy}>
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
      <Row glutter={[16, 16]}>
        <Col span={6}>
          <p>Filter by :</p>
        </Col>
        <Col span={6}>
          <Dropdown overlay={filterMenu}>
            <a className="ant-dropdown-link">
              {filterText} <DownOutlined />
            </a>
          </Dropdown>
        </Col>
      </Row>
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

  const data = projectsFilter();

  return (
    <React.Fragment>
      <Row>
        <Col span={12}>
          <p>{data.length} project(s) | {Array.from(new Set(data.filter((project) => project.client !== undefined).map((project) => project.client.id))).length} client(s)</p>
        </Col>
        <Col span={12}>
          {filterComponent}
        </Col>
      </Row>
      <List
        id="tk_List"
        grid={{ gutter: 32, column: 3 }}
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
                    <p>{item.name}<span>{item.client ? '| ' + item.client.name : ''}</span></p>
                    <p>{item.publicAccess ?   <UnlockOutlined/>  : <LockFilled/> }<span>{ item.publicAccess ? ' Public' : ' Private project' }</span></p>
                  </div>
                </Space>
              }
              extra={[
                <Tooltip title="View" key="view">
                  <Button type="link" size="small" ghost shape="circle" icon={<EyeFilled />} href={`/projects/${item.id}`}/>
                </Tooltip>,
                <Tooltip title="Edit" key="edit">
                  <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>} href={`/projects/${item.id}/edit`}/>
                </Tooltip>
              ]}
              actions={[
                <Collapse bordered={false} expandIconPosition={'right'} key="projects">
                  <Panel header={<Space size="small"><UserOutlined />{item.users.length}{ item.users.length === 1 ? 'member' : 'members' }</Space>} key="members">
                    <List
                      className={'tk_Project_MemberList'}
                      dataSource={item.users.sort(((a, b) => memberComparator(a, b) ))}
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
      />
    </React.Fragment>
  );
};
export default ProjectList;
