import React from 'react';
import {Alert, Avatar, Button, Card, Collapse, List, Spin} from 'antd';
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
import ProjectMemberTag  from './ProjectMemberTag';

const { Panel } = Collapse;

const ProjectList = () => {

  const projectsResponse = useTimeKeeperAPI('/api/projects');

  const projects = () => projectsResponse.data;

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

  return (
    <React.Fragment>
      <p>{projects().length} project(s) | {Array.from(new Set(projects().filter((project) => project.client !== undefined).map((project) => project.client.id))).length} client(s)</p>
      <List
        id="tk_List"
        grid={{ gutter: 32, column: 3 }}
        dataSource={projects()}
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
