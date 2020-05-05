import React, {useState} from 'react';
import {Alert, Avatar, Button, Form, Input, message, Radio, Select, Space, Spin, Table} from 'antd';
import {useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import {Link, Redirect} from 'react-router-dom';


const {TextArea} = Input;
const {Option} = Select;


const columns = [
  {
    title: '',
    dataIndex: 'picture',
    key: 'picture',
    width: 60,
    align: 'right',
    render: (value) => renderAvatar(value),
  },
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: 'Manager',
    dataIndex: 'manager',
    key: 'manager',
    render: (value) => value ? "Manager" : "Member"
  }
];

const renderAvatar = (value) => <Avatar src={value}/>;

const NewProjectForm = () => {

  const [projectCreated, setProjectCreated] = useState(false);

  const [projectRequest, setProjectRequest] = useState({
    publicAccess: true,
    billable: false,
    selectedClientId: undefined,
    users: []
  });

  const clientsResponse = useTimeKeeperAPI('/api/clients');
  const projectsResponse = useTimeKeeperAPI('/api/projects');
  const usersResponse = useTimeKeeperAPI('/api/users');

  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/projects', (form => form), setProjectCreated);

  const [duplicatedNameError, setDuplicatedNameError] = useState(false);

  const formRef = React.createRef();

  const addMember = (id) => {
    const user = usersResponse.data.find(user => user.id === id);
    const userProject = {
      ...user,
      manage: false
    };
    setProjectRequest({...projectRequest, users: projectRequest.users.concat(userProject)});
  };
  const filterUsers = (users, usersToRemove) => {
    const usersToRemoveIds = usersToRemove.map(user => user.id);
    const filtered = users.filter(user => !usersToRemoveIds.includes(user.id));
    console.log(filtered);
    return filtered;
  };

  if (projectCreated) {
    message.success('Project was created');
    return (
      <React.Fragment>
        <Redirect to="/projects"/>
      </React.Fragment>
    );
  }

  if (timeKeeperAPIPost.error) {
    const {response} = timeKeeperAPIPost.error;
    const {status, url} = response;
    const errMsg = `Server error HTTP Code:${status}  for url: ${url}`;
    return (
      <React.Fragment>
        <Alert
          message="Unable to save the new Project"
          description={errMsg}
          type="error"
          closable
          style={{marginBottom: 10}}
        />
      </React.Fragment>
    );
  }

  if (clientsResponse.data && projectsResponse.data && usersResponse.data) {
    const projectsName = projectsResponse.data.map(project => project.name);
    const onChangeName = (event) => setDuplicatedNameError(projectsName.includes(event.target.value));
    console.log(projectRequest)
    return (
      <React.Fragment>
        <div style={{borderTop: '1px solid rgba(216, 216, 216, 0.1)', marginTop: 48}}>&nbsp;</div>
        <Form
          labelCol={{span: 4}}
          wrapperCol={{span: 14}}
          layout="horizontal"
          initialValues={projectRequest}
          onFinish={timeKeeperAPIPost.run}
          ref={formRef}
        >
          <Form.Item
            label="Name"
            name="name"
            validateStatus={duplicatedNameError && 'error'}
            help={duplicatedNameError && 'A project already use this name'}
            rules={[
              {
                required: true,
              },
            ]}
          >
            <Input
              placeholder="Project's name"
              onChange={onChangeName}
            />
          </Form.Item>
          <Form.Item
            label="Description"
            name="description"
          >
            <TextArea
              rows={4}
              placeholder="A short description about this project"
            />
          </Form.Item>

          <Form.Item
            label="Client"
            name="clientId"
          >
            <Select style={{width: 200}} >
              <Option key={'option-client-empty'} value={null}> </Option>
              {clientsResponse.data.map(client =>
                <Option key={`option-client-${client.id}`} value={client.id}>{client.name}</Option>)}
            </Select>
          </Form.Item>

          <Form.Item
            label="Billable"
            name="billable"
          >
            <Radio.Group>
              <Radio value={true}>Yes</Radio>
              <Radio value={false}>No</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="Project type"
            name="publicAccess"
          >
            <Radio.Group>
              <Radio value={true}>Public</Radio>
              <Radio value={false}>Private</Radio>
            </Radio.Group>
          </Form.Item>

          <Form.Item
            label="Users"
            name="selectedUser"
            getValueProps={(value) => console.log(value)}
          >
            <Select
              showSearch
              style={{ width: 200 }}
              placeholder="Select a client"
              optionFilterProp="children"
              filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {filterUsers(usersResponse.data, projectRequest.users).map(user => <Option key={`option-user-${user.id}`} value={user.id}>{user.name}</Option>)}
            </Select>
          </Form.Item>
          <Button onClick={() => addMember(formRef.current.getFieldValue('selectedUser'))}>Add a collaborator</Button>

          <Form.Item
            label="Members"
            name="users"
          >
            <Table id="tk_Table"
                   dataSource={projectRequest.users}
                   columns={columns}
            />
          </Form.Item>

          <Form.Item>
            <Space size="middle" style={{right: 0, position: 'absolute'}}>
              <Link key="cancelLink" to={'/projects'}>
                <Button htmlType="button">
                  Cancel
                </Button>
              </Link>
              <Button type="primary" htmlType="submit">
                Submit
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </React.Fragment>
    );
  }

  if (clientsResponse.loading || projectsResponse.loading || usersResponse.loading) {
    return (
      <React.Fragment>
        <Spin size="large">
          <Form
            labelCol={{span: 4}}
            wrapperCol={{span: 14}}
            layout="horizontal"
          >
            <Form.Item label="Name" name="name">
              <Input placeholder="Loading data from server..."/>
            </Form.Item>
            <Form.Item label="Description" name="description">
              <TextArea
                rows={4}
                placeholder="Loading data from server..."
              />
            </Form.Item>
          </Form>
        </Spin>

      </React.Fragment>
    );
  }

  if(clientsResponse.error || projectsResponse.error || usersResponse.error){
    return (
      <React.Fragment>
        <Alert title='Server error'
          message='Failed to load the data'
          type='error'
        />
      </React.Fragment>
    );
  }

};

export default NewProjectForm;