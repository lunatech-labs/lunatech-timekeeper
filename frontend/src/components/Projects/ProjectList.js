import React from 'react';
import {Alert, Avatar, Button, Card, Collapse, List, Spin} from 'antd';
import logo from '../../img/logo_timekeeper_homepage.png';
import {useTimeKeeperAPI} from '../../utils/services';
import EditFilled from '@ant-design/icons/lib/icons/EditFilled';
import Tooltip from 'antd/lib/tooltip';
import Space from 'antd/lib/space';
import Tag from 'antd/lib/tag';
import UserOutlined from '@ant-design/icons/lib/icons/UserOutlined';
import Meta from 'antd/lib/card/Meta';

import './ProjectList.less';

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

  return (
    <React.Fragment>
      <p>{projects().length} Projects</p>
      <List
        id="tk_List"
        grid={{ gutter: 32, column: 3 }}
        dataSource={projects()}
        renderItem={item => (
          <List.Item key={item.id}>
            <Card
              className={'shadow'}
              bordered={false}
              title={
                <Space size={'middle'}>
                  <Avatar src={logo} shape={'square'} size="large"/>
                  <div>{item.name}<br/><span className={'subtitle'}>subtitle</span></div>
                </Space>
              }
              extra={[
                <Tooltip title="Edit" key="edit">
                  <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>} href={`/projects/${item.id}/edit`}/>
                </Tooltip>
              ]}
              actions={[
                <Collapse bordered={false} expandIconPosition={'right'} key="projects">
                  <Panel header={<Space size="small"><UserOutlined />{'Members'}</Space>} key="1">
                    <List
                      className={'projectList'}
                      dataSource={item.projects}
                      renderItem={projectItem => (
                        <List.Item>{projectItem.name} <Tag className="usersTag" icon={<UserOutlined />}>todo tag</Tag></List.Item>
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
