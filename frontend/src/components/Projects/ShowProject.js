import React, {useState} from 'react';
import {Avatar, Col, Modal, Row, Typography} from 'antd';
import './ShowProject.less';
import PropTypes from 'prop-types';
import {DesktopOutlined, DollarOutlined, LockOutlined, UserOutlined,} from '@ant-design/icons';
import TitleSection from '../Title/TitleSection';
import CardLg from '../Card/CardLg';
import CardMember from '../Card/CardMember';
import TagMember from '../Tag/TagMember';
import SnippetsOutlined from "@ant-design/icons/lib/icons/SnippetsOutlined";
import Tooltip from "antd/es/tooltip";
import TagProjectClient from "../Tag/TagProjectClient";
import ShowTimesheet from "../TimeSheet/ShowTimesheet";

const {Title} = Typography;

const ShowProject = ({project}) => {

  const [modalVisible, setModalVisible] = useState(false);
  const [selectedMember, setSelectedMember] = useState();
  const Members = () => {
    if (!project.users || project.users.length === 0) {
      return 'No user added yet';
    } else {
      const users = project.users.sort((u1, u2) => {
        const res = u2.manager - u1.manager;
        return res === 0 ? u1.name.localeCompare(u2.name) : res;
      });
      return users.map(user =>
        <CardMember key={`project-member-${user.id}`}>
          <div>
            <Avatar src={user.picture}/>
            <p>{user.name}</p>
          </div>
          <TagMember isManager={user.manager}/>
          <Tooltip title='Time sheet'>
            <SnippetsOutlined onClick={() => {
              setModalVisible(true);
              setSelectedMember(user)
            }}/>
          </Tooltip>
        </CardMember>
      );
    }
  };


  const ModalTimesheet = () => {
    return (
      <Modal visible={modalVisible} closable={true} footer={null} onCancel={(e) => setModalVisible(false)}>
        <ShowTimesheet project={project} member={selectedMember}/>
      </Modal>
    )
  };

  return (
    <div>
      <ModalTimesheet />
      <CardLg>
        <Title level={2}>{project.name}</Title>
        <Row gutter={32}>
          <Col span={12}>
            <TitleSection title="Information"/>
            <Row gutter={32}>
              <Col span={12}>
                <p className="tk_ProjectAtt"><DesktopOutlined/> Client : <TagProjectClient client={project.client}/></p>
                <p className="tk_ProjectAtt"><UserOutlined/> Members : {project.users ? project.users.length : 0}</p>
              </Col>
              <Col span={12}>
                <p className="tk_ProjectAtt"><DollarOutlined/> Billable : {project.billable ? 'Yes' : 'No'}</p>
                <p className="tk_ProjectAtt"><LockOutlined/> Project type
                  : {project.publicAccess ? 'Public' : 'Private'}</p>
              </Col>
            </Row>
            <Col span={24}>
              <p className="tk_ProjectDesc">{project.description}</p>
            </Col>
          </Col>
          <Col span={12}>
            <TitleSection title="Members"/>
            <Members/>
          </Col>
        </Row>
      </CardLg>
    </div>
  );
};

ShowProject.propTypes = {
  project: PropTypes.object.isRequired
};

export default ShowProject;