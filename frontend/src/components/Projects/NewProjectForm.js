import React, {useEffect, useState} from 'react';
import {Link, Redirect} from 'react-router-dom';
import {useTimeKeeperAPI, useTimeKeeperAPIPost} from '../../utils/services';
import {Alert, Avatar, Button, Checkbox, Form, Input, message, Radio, Select, Space, Spin, Row, Col, Tooltip} from 'antd';
import {
  DeleteFilled,
  CloseOutlined,
  CheckOutlined,
} from '@ant-design/icons';
import './NewProjectForm.less';
import PropTypes from 'prop-types';
import TitleSection from '../Title/TitleSection';


const {TextArea} = Input;
const {Option} = Select;
const NewProjectForm = () => {

  const [projectCreated, setProjectCreated] = useState(false);

  const initialValues = {
    name: '',
    description: '',
    publicAccess: true,
    billable: false,
    clientId: null,
    users: []
  };

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

  const [form] = Form.useForm();

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
    const projectsName = projectsResponse.data.map(project => project.name.toLowerCase().trim());
    const UserName = ({value = {}}) => {
      return (<p>{usersResponse.data.find(u => u.id === value).name}</p>);
    };
    UserName.propTypes = {
      value: PropTypes.string
    };
    const UserPicture = ({value}) => {
      return (<Avatar src={usersResponse.data.find(u => u.id === value).picture}/>);
    };
    UserPicture.propTypes = {
      value: PropTypes.string
    };
    return (
      <Form
        id="tk_Form"
        layout="vertical"
        initialValues={initialValues}
        onFinish={timeKeeperAPIPost.run}
        form={form}
      >
        <div className="tk_CardLg">
          <Row gutter={16}>
            <Col className="gutter-row" span={12}>
              <TitleSection title="Information"/>
              <Form.Item
                label="Name :"
                name="name"
                hasFeedback
                rules={[
                  {
                    required: true,
                  },
                  () => ({
                    validator(rule, value) {
                      const name = value.toLowerCase().trim();
                      if (!projectsName.includes(name)) {
                        return Promise.resolve();
                      }
                      return Promise.reject('A project already use this name');
                    },
                  }),
                ]}
              >
                <Input
                  placeholder="Project's name"
                />
              </Form.Item>
              <Form.Item
                label="Description :"
                name="description"
              >
                <TextArea
                  rows={4}
                  placeholder="A short description about this project"
                />
              </Form.Item>
              <Form.Item
                label="Client :"
                name="clientId"
              >
                <Select
                  className="tk_Select"
                  placeholder="Select a client"
                >
                  <Option key={'option-client-empty'} value={null}><i>None</i></Option>
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
              <TitleSection title="Members"/>
              <Form.List
                label="Users"
                name="users"
              >
                {(fields, {add, remove}) => {
                  return (
                    <Form.Item label="Users :">
                      <Form.Item>
                        <Select
                          className="tk_Select"
                          showSearch
                          placeholder="Select a user"
                          optionFilterProp="children"
                          onSelect={(value) => add({id: value, manager: false})}
                          filterOption={(input, option) =>
                            option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                          }
                        >
                          {usersResponse.data.map(user =>
                            <Option key={`option-user-${user.id}`}
                              disabled={isIncluded(user.id, form.getFieldValue('users'))}
                              value={user.id}>{user.name}</Option>)
                          }
                        </Select>
                      </Form.Item>
                      <p className="tk_LabelForm">Added members :</p>
                      {fields.length === 0 ? 'There is no members on the project' :
                        fields.map((field, index) => {
                          const id = [index, 'id'];
                          const manager = [index, 'manager'];
                          return (
                            <div id="tk_Card_MemberList" key={field.key} required={false}>
                              <Form.Item>
                                <div>
                                  <Form.Item
                                    noStyle
                                    name={id}
                                  >
                                    <UserPicture/>
                                  </Form.Item>
                                  <Form.Item
                                    noStyle
                                    name={id}
                                    rules={[{required: true}]}
                                  >
                                    <Input type="hidden"/>
                                  </Form.Item>
                                  <Form.Item
                                    name={id}
                                  >
                                    <UserName/>
                                  </Form.Item>
                                </div>
                                <div>
                                  <Form.Item
                                    noStyle
                                    name={manager}
                                    valuePropName="checked"
                                  >
                                    <Tooltip title="Team leader">
                                      <Checkbox className="tk_Crown_Checkbox"/>
                                    </Tooltip>
                                  </Form.Item>
                                  <DeleteFilled
                                    className="dynamic-delete-button"
                                    style={{margin: '0 8px'}}
                                    onClick={() => {
                                      remove(index);
                                    }}
                                  />
                                </div>
                              </Form.Item>
                            </div>
                          );
                        })
                      }
                    </Form.Item>
                  );
                }}
              </Form.List>
            </Col>
          </Row>
        </div>
        <Form.Item>
          <Space className="tk_JcFe" size="middle" align="center">
            <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/projects'}><CloseOutlined />Cancel</Link>
            <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit"><CheckOutlined />Submit</Button>
          </Space>
        </Form.Item>
      </Form>
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