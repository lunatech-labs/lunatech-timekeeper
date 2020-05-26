import React, {useState} from 'react';
import {Alert, AutoComplete, Avatar, Button, Card, Collapse, List, Spin} from 'antd';
import logo from '../../img/logo_timekeeper_homepage.png';
import './ClientList.less';
import {useTimeKeeperAPI} from '../../utils/services';
import FolderFilled from '@ant-design/icons/lib/icons/FolderFilled';
import EditFilled from '@ant-design/icons/lib/icons/EditFilled';
import Tooltip from 'antd/lib/tooltip';
import Space from 'antd/lib/space';
import Tag from 'antd/lib/tag';
import UserOutlined from '@ant-design/icons/lib/icons/UserOutlined';
import Input from 'antd/lib/input';
import SearchOutlined from '@ant-design/icons/lib/icons/SearchOutlined';
import Meta from 'antd/lib/card/Meta';

const { Panel } = Collapse;

const ClientList = () => {

  const clientsResponse = useTimeKeeperAPI('/api/clients');

  const [value, setValue] = useState('');

  const onSearch = searchText => setValue(searchText);

  const clientsFiltered = () => clientsResponse.data.filter(d => d.name.toLowerCase().includes(value.toLowerCase())).sort((a,b)=>{
    if(a.name.toLowerCase() < b.name.toLowerCase()){return -1;}
    if(a.name.toLowerCase() > b.name.toLowerCase()){return 1;}
    return 0;
  });

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
      <div className="tk_SubHeader">
        <p>{clientsFiltered().length} Clients</p>
        <div className="tk_SubHeader_RightPart">
          <div className="tk_Search_Input">
            <AutoComplete onSearch={onSearch}>
              <Input size="large" placeholder="Search in clients" allowClear  prefix={<SearchOutlined />} />
            </AutoComplete>
          </div>
        </div>
      </div>

      <List
        id="tk_List"
        grid={{ gutter: 32, column: 3 }}
        dataSource={clientsFiltered()}
        renderItem={item => (
          <List.Item key={item.id}>
            <Card
              id="tk_Card_Sm"
              bordered={false}
              title={
                <Space size={'middle'}>
                  <Avatar src={logo} shape={'square'} size="large"/>
                  <div className="tk_Card_Sm_Header">
                    <p>{item.name}</p>
                    <p>{item.projects.length} projects</p>
                  </div>
                </Space>
              }
              extra={[
                <Tooltip title="Edit" key="edit">
                  <Button type="link" size="small" ghost shape="circle" icon={<EditFilled/>} href={`/clients/${item.id}/edit`}/>
                </Tooltip>
              ]}
              actions={[
                <Collapse bordered={false} expandIconPosition={'right'} key="projects">
                  <Panel header={<Space size="small"><FolderFilled />{'List of projects'}</Space>} key="1">
                    <List
                      className={'tk_Project_MemberList'}
                      dataSource={item.projects}
                      renderItem={projectItem => (
                        <List.Item>{projectItem.name} <Tag id="tk_UsersTag" icon={<UserOutlined />}>{projectItem.userCount}</Tag></List.Item>
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

export default ClientList;