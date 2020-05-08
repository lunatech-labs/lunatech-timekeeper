import React, {useEffect, useState} from 'react';
import {Row, Col, Alert, Button, Form, Input, message, Radio, Select, Space, Spin} from 'antd';
import {useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import {Link, Redirect} from 'react-router-dom';
import './NewProjectForm.less';


const {TextArea} = Input;
const {Option} = Select;

const staticUsers = [{
  id: 1,
  name: 'Billy Bob'
}, {
  id: 2,
  name: 'Harry Potter'
}];

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

  const timeKeeperAPIPost = useTimeKeeperAPIPost('/api/projects', (form => form), setProjectCreated);

  const [duplicatedNameError, setDuplicatedNameError] = useState(false);

  const onChangeUsers = (value) => {
    const userProject = {
      id: value,
      manage: false
    };
    setProjectRequest({...projectRequest, users: projectRequest.users.concat(userProject)});
  };

  useEffect(() => {
    if(!projectCreated) {
      return;
    }
    message.success('Project was created');
  }, [projectCreated]);

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

  if (clientsResponse.data && projectsResponse.data) {
    const projectsName = projectsResponse.data.map(project => project.name);
    const onChangeName = (event) => setDuplicatedNameError(projectsName.includes(event.target.value));
    return (

      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={projectRequest}
        onFinish={timeKeeperAPIPost.run}
      >
        <div className="tk_CardLg">
          <Row gutter={16}>
            <Col className="gutter-row" span={12}>
              <p className="tk_FormTitle">Informations</p>
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
                <Select
                  className="tk_Select"
                  placeholder="Select a client"
                >
                  <Option key={'option-client-empty'} value={null}></Option>
                  {clientsResponse.data.map(client => <Option key={`option-client-${client.id}`} value={client.id}>{client.name}</Option>)}
                </Select>
              </Form.Item>
              <Row gutter={16}>
                <Col className="gutter-row" span={12}>
                  <Form.Item
                    label="Billable"
                    name="billable"
                  >
                    <Radio.Group>
                      <Radio value={true}>Yes</Radio>
                      <Radio value={false}>No</Radio>
                    </Radio.Group>
                  </Form.Item>
                </Col>
                <Col className="gutter-row" span={12}>
                  <Form.Item
                    label="Project type"
                    name="publicAccess"
                  >
                    <Radio.Group>
                      <Radio value={true}>Public</Radio>
                      <Radio value={false}>Private</Radio>
                    </Radio.Group>
                  </Form.Item>
                </Col>
              </Row>
            </Col>
            <Col className="gutter-row" span={12}>
              <p className="tk_FormTitle">Members</p>
              <Form.Item
                label="Users"
                name="users"
              >
                <Select
                  showSearch
                  placeholder="Select a user"
                  optionFilterProp="children"
                  onChange={onChangeUsers}
                  filterOption={(input, option) =>
                    option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                  }
                >
                  {staticUsers.map(user => <Option key={`option-user-${user.id}`} value={user.id}>{user.name}</Option>)}
                </Select>
              </Form.Item>
            </Col>
          </Row>
        </div>
        <Form.Item>
          <Space size="middle" style={{right: 0, position: 'absolute'}}>
            <Link key="cancelLink" to={'/projects'}>
              <Button htmlType="button">Cancel</Button>
            </Link>
            <Button type="primary" htmlType="submit">Submit</Button>
          </Space>
        </Form.Item>
      </Form>





    );
  }

  if (clientsResponse.loading || projectsResponse.loading) {
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

  if(clientsResponse.error || projectsResponse.error){
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