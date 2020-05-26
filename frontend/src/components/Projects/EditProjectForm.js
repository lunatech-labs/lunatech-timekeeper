import React, {useEffect, useState} from 'react';
import {Alert, Avatar, Button, Checkbox, Form, Input, message, Radio, Select, Space, Spin, Row, Col} from 'antd';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import DeleteOutlined from '@ant-design/icons/lib/icons/DeleteOutlined';
import PropTypes from 'prop-types';
import './EditProjectForm.less';
import TitleSection from '../Title/TitleSection';
import {CheckOutlined, CloseOutlined} from '@ant-design/icons';
import '../../components/Button/BtnGeneral.less';


const {TextArea} = Input;
const {Option} = Select;
const EditProjectForm = () => {

  const [projectUpdated, setProjectUpdated] = useState(false);

  const projectIdSlug = useRouteMatch({
    path: '/projects/:id',
    strict: true,
    sensitive: true
  });

  const projectResponse = useTimeKeeperAPI('/api/projects/' + projectIdSlug.params.id);

  useEffect(() => {
    if (!projectUpdated) {
      return;
    }
    message.success('Project was updated');
  }, [projectUpdated]);

  const clientsResponse = useTimeKeeperAPI('/api/clients');
  const projectsResponse = useTimeKeeperAPI('/api/projects');
  const usersResponse = useTimeKeeperAPI('/api/users');
  const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/projects/' + projectIdSlug.params.id, (form => form), setProjectUpdated);

  const [form] = Form.useForm();

  const isIncluded = (id, users) => users.filter(user => user.id === id).length !== 0;

  if (projectUpdated) {
    return (
      <React.Fragment>
        <Redirect to="/projects"/>
      </React.Fragment>
    );
  }

  if (timeKeeperAPIPut.error) {
    const {response} = timeKeeperAPIPut.error;
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

  if (clientsResponse.data && projectsResponse.data && usersResponse.data && projectResponse.data) {
    const initialValues = {
      ...projectResponse.data,
      clientId: (projectResponse.data.client && projectResponse.data.client.id) || null
    };
    const projectsName = projectsResponse.data.map(project => project.name.toLowerCase().trim());
    const UserName = ({value = {}}) => {
      return (<span>{usersResponse.data.find(u => u.id === value).name}</span>);
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
        onFinish={timeKeeperAPIPut.run}
        form={form}
      >
        <div className="tk_CardLg">
          <Row gutter={16}>
            <Col className="gutter-row" span={12}>
              <TitleSection title="Information"/>
              <Form.Item
                label="Name"
                name="name"
                hasFeedback
                rules={[
                  {
                    required: true,
                  },
                  () => ({
                    validator(rule, value) {
                      const name = value.toLowerCase().trim();
                      if (!projectsName.includes(name) || name === initialValues.name.toLowerCase()) {
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
                                    style={{marginRight: '80px'}}
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
                                    <Checkbox className="tk_Crown_Checkbox"/>
                                  </Form.Item>
                                  <DeleteOutlined
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

  if (clientsResponse.loading || projectsResponse.loading || usersResponse.loading || projectResponse.loading) {
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

  if (clientsResponse.error || projectsResponse.error || usersResponse.error || projectResponse.error) {
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

export default EditProjectForm;