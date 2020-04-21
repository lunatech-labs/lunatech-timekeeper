import React from 'react';
import {Alert, Avatar, Button, List, PageHeader, Spin} from 'antd';
import {EditOutlined, MoreOutlined} from '@ant-design/icons';
import {Link} from 'react-router-dom';
import logo from '../../img/logo_timekeeper_homepage.png';
import {useTimeKeeperAPI} from '../../utils/services';

const ClientList = () => {

  const clientsResponse = useTimeKeeperAPI('/api/clients');
  const projectsResponse = useTimeKeeperAPI('/api/projects');

  const projectsIdToProjects = (projectsId) => {
    if (projectsResponse.data) {
      return projectsResponse.data.filter(
          project => projectsId.includes(project.id));
    }
    return [];
  };

  const renderProjects = (projectsId) => projectsIdToProjects(projectsId).
      map(project => project.name).
      join(' | ');

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
                 description={clientsResponse.error}
          />
        </React.Fragment>
    );
  }

  return (
      <React.Fragment>
        <PageHeader title="Clients" subTitle={clientsResponse.data.length}/>
        <List
            itemLayout="horizontal"
            dataSource={clientsResponse.data}
            renderItem={item => (
                <List.Item key={item.id}
                           actions={[
                             <Link key="editLink" to={`/clients/${item.id}`}>
                               <Button type="default"
                                       icon={<EditOutlined/>}>Edit</Button>
                             </Link>,
                             <Link key="moreLink" to={`/clients/${item.id}`}>
                               <Button type="default" icon={<MoreOutlined/>}/>
                             </Link>,
                           ]}
                >
                  <List.Item.Meta
                      avatar={
                        <Avatar src={logo}/>
                      }
                      title={item.name}
                      description={item.description}
                  />
                  <div>{renderProjects(item.projectsId)}</div>
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