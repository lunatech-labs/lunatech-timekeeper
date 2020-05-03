import React from 'react';
import {Alert, Avatar, Button, Card, List, PageHeader, Spin, Collapse} from 'antd';
import {Link} from 'react-router-dom';
import logo from '../../img/logo_timekeeper_homepage.png';
import {useTimeKeeperAPI} from '../../utils/services';
import Meta from 'antd/es/card/Meta';
import FolderOpenOutlined from '@ant-design/icons/es/icons/FolderOpenOutlined';
import Badge from 'antd/es/badge';
import EditFilled from '@ant-design/icons/es/icons/EditFilled';
import Tooltip from 'antd/es/tooltip';

const { Panel } = Collapse;

const ClientList = () => {

  const clientsResponse = useTimeKeeperAPI('/api/clients');

  const renderProjects = (projects) => projects.map(project => project.name).join(' | ');

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
      <PageHeader title="Clients" subTitle={clientsResponse.data.length}/>
      <List
        grid={{ gutter: 32, column: 3 }}
        dataSource={clientsResponse.data}
        renderItem={item => (
          <List.Item key={item.id}>
            <Card
              bordered={false}
              title={<div><Avatar src={logo} shape={'square'}/> {item.name}</div>}
              extra={[
                <Tooltip title="Edit">
                  <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>} href={`/clients/${item.id}`}/>
                </Tooltip>
              ]}
              actions={[
                <Collapse bordered={false} expandIconPosition={'right'}>
                  <Panel header={<div><FolderOpenOutlined /> {"List of projects"}</div>} key="1">
                    <List
                      dataSource={item.projects}
                      renderItem={projectItem => (
                        <List.Item>
                          <p>{projectItem.name} <Badge count={projectItem.userCount} /></p>
                        </List.Item>
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