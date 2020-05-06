import React, {useEffect, useState} from 'react';
import {Alert, Avatar, Button, Checkbox, Form, Input, message, Radio, Select, Space, Spin} from 'antd';
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
    title: '',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '',
    dataIndex: 'manager',
    key: 'manager',
    render: (value) => <div>{value ? "Manager" : "Member"}</div>
  }
];

const renderAvatar = (value) => <Avatar src={value}/>;

const NewProjectForm = () => {

  const [projectCreated, setProjectCreated] = useState(false);

  const initialProject = {
    publicAccess: true,
    billable: false,
    selectedClientId: undefined,
    users: [{id: 1, name: 'test', manager: true}]
  }

  useEffect(() => {
    if (!projectCreated) {
      return;
    }
    message.success('Project was created');
  }, [projectCreated]);

  const clientsResponse = useTimeKeeperAPI('/api/clients');
  const projectsResponse = useTimeKeeperAPI('/api/projects');
  const usersResponse = useTimeKeeperAPI('/api/users');
  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/projects', (form => form), setProjectCreated);

  const [duplicatedNameError, setDuplicatedNameError] = useState(false);

  const [form] = Form.useForm();

  const addMember = (id) => {
    const user = usersResponse.data.find(user => user.id === id);
    const userProject = {
      ...user,
      manage: false
    };
    const usersForm = form.getFieldValue('users');
    console.log(usersForm)
    form.setFieldsValue({users: usersForm.concat(userProject)})
  };
  const isIncluded = (id, users) => users.filter(user => user.id === id).length !== 0;

  if (projectCreated) {
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
    return (
      <React.Fragment>
        <div style={{borderTop: '1px solid rgba(216, 216, 216, 0.1)', marginTop: 48}}>&nbsp;</div>
        <Form
          labelCol={{span: 4}}
          wrapperCol={{span: 14}}
          layout="horizontal"
          initialValues={initialProject}
          onFinish={timeKeeperAPIPost.run}
          form={form}
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
            <Select style={{width: 200}}>
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



          <Form.List name="users">
            {(fields, {add, remove}) => {
              return (
                <div>
                  {fields.map(field => (
                    <div key={0}>
                      {console.log(field)}
                      <Form.Item name={field.id}>
                        <Input/>
                      </Form.Item>
                      <Form.Item name="name">
                        <Input/>
                      </Form.Item>
                      <Form.Item name="manager">
                        <Checkbox/>
                      </Form.Item>
                    </div>
                  ))}
                </div>
              )
            }}
          </Form.List>


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

  if (clientsResponse.error || projectsResponse.error || usersResponse.error) {
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