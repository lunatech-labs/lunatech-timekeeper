import React from 'react';
import {Alert, Avatar, Button, Card, Collapse, List, Spin} from 'antd';
import {Link} from 'react-router-dom';
import logo from '../../img/logo_timekeeper_homepage.png';
import './ClientList.less';
import {useTimeKeeperAPI} from '../../utils/services';
import Meta from 'antd/es/card/Meta';
import FolderFilled from '@ant-design/icons/es/icons/FolderFilled';
import EditFilled from '@ant-design/icons/es/icons/EditFilled';
import Tooltip from 'antd/es/tooltip';
import Space from 'antd/es/space';
import Tag from 'antd/es/tag';
import UserOutlined from '@ant-design/icons/es/icons/UserOutlined';

const { Panel } = Collapse;

const ClientList = () => {

  const clientsResponse = useTimeKeeperAPI('/api/clients');

  if (clientsResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <p>Loading client</p>
        </Spin>
      </React.Fragment>

    );
  }
  if (clientsResponse.error) {
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the list of clients'
          type='error'
          description='check that the authenticated User has role [user] on Quarkus'
        />
      </React.Fragment>
    );
  }

  return (
    <React.Fragment>
      <div>
        <p>{clientsResponse.data.length} Clients</p>
      </div>
      <List
        id="tk_List"
        grid={{ gutter: 32, column: 3 }}
        dataSource={clientsResponse.data}
        renderItem={item => (
          <List.Item key={item.id}>
            <Card
              className={'shadow'}
              bordered={false}
              title={
                <Space size={'middle'}>
                  <Avatar src={logo} shape={'square'} size="large"/>
                  <div>{item.name}<br/><span className={'subtitle'}>{item.projects.length} projects</span></div>
                </Space>
              }
              extra={[
                <Tooltip title="Edit" key="edit">
                  <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>} href={`/clients/${item.id}`}/>
                </Tooltip>
              ]}
              actions={[
                <Collapse bordered={false} expandIconPosition={'right'} key="projects">
                  <Panel header={<Space size="small"><FolderFilled />{'List of projects'}</Space>} key="1">
                    <List
                      className={'projectList'}
                      dataSource={item.projects}
                      renderItem={projectItem => (
                        <List.Item>{projectItem.name} <Tag className="usersTag" icon={<UserOutlined />}>{projectItem.userCount}</Tag></List.Item>
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
      <Link to="/clients/new">
        <Button type="primary">Create new client</Button>
      </Link>


    </React.Fragment>
  );
};

export default ClientList;