/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useContext, useEffect, useState} from 'react';
import {Alert, Button, Checkbox, Form, Input, message, Radio, Select, Space, Spin, Row, Col} from 'antd';
import {useTimeKeeperAPI, useTimeKeeperAPIPut} from '../../utils/services';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import DeleteOutlined from '@ant-design/icons/lib/icons/DeleteOutlined';
import PropTypes from 'prop-types';
import './EditProjectForm.less';
import TitleSection from '../Title/TitleSection';
import '../../components/Button/BtnGeneral.less';
import TkUserAvatar from '../Users/TkUserAvatar';
import {ForbiddenRoute} from '../../routes/utils';
import {UserContext} from '../../context/UserContext';
import {useKeycloak} from '@react-keycloak/web';
import {canEditProject} from '../../utils/rights';


const {TextArea} = Input;
const {Option} = Select;
const EditProjectForm = ({...rest}) => {

  const [projectUpdated, setProjectUpdated] = useState(false);
  const {currentUser} = useContext(UserContext);

  const [keycloak] = useKeycloak();
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
    if(!canEditProject(projectResponse.data, currentUser, keycloak)) {
      return (
        <ForbiddenRoute {...rest}/>
      );
    }

    const clientsSorted = () => clientsResponse.data.sort((a,b)=>{
      if(a.name.toLowerCase() < b.name.toLowerCase()){return -1;}
      if(a.name.toLowerCase() > b.name.toLowerCase()){return 1;}
      return 0;
    });
    const initialValues = {
      ...projectResponse.data,
      clientId: (projectResponse.data.client && projectResponse.data.client.id) || null
    };
    const UserName = ({value = {}}) => {
      return (<span>{usersResponse.data.find(u => u.id === value).name}</span>);
    };
    UserName.propTypes = {
      value: PropTypes.string
    };
    const UserPicture = ({value}) => {
      const currentUser = usersResponse.data.find(u => u.id === value);
      return (<TkUserAvatar picture={currentUser.picture} name={currentUser.name}/>);
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
        <Form.Item
          noStyle
          name="version"
          rules={[{required: true}]}
        ></Form.Item>
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
                  }
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
                  {clientsSorted().map(client => <Option key={`option-client-${client.id}`} value={client.id}>{client.name}</Option>)}
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
            <Link id="btnCancelEditProject" className="tk_Btn tk_BtnSecondary" key="cancelLink" to={'/projects'}>Cancel</Link>
            <Button id="btnSubmitEditProject" className="tk_Btn tk_BtnPrimary" htmlType="submit">Submit</Button>
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