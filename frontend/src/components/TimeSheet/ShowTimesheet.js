import React from 'react';
import {Col, Divider, Row, Tag} from "antd";
import TagProjectClient from "../Tag/TagProjectClient";
import TitleSection from "../Title/TitleSection";
import './ShowTimesheet.less';
import {DesktopOutlined, DollarOutlined, LockOutlined, UserOutlined} from "@ant-design/icons";
import ClockCircleOutlined from '@ant-design/icons/lib/icons/ClockCircleOutlined';
import CardMember from "../Card/CardMember";
import ProjectMemberTag from "../Projects/ProjectMemberTag";
const ShowTimesheet = ({timesheet, project, member}) => {
  return (
    <div>
      <h1>Individual time sheet</h1>
      <h2>{project.name}</h2><TagProjectClient client={project.client} />
      <div><ProjectMemberTag member={member}/></div>
      <Divider/>
          <Row gutter={32}>
            <Col span={12}>
              <p className="tk_ProjectAtt"><ClockCircleOutlined/> TimeUnit : Half day</p>
              <p className="tk_ProjectAtt"><UserOutlined/> End date : 2020/06/30</p>
            </Col>
            <Col span={12}>
              <p className="tk_ProjectAtt"><DollarOutlined/> Number of days : 21</p>
              <p className="tk_ProjectAtt"><DollarOutlined/> Billable : yes</p>
            </Col>
          </Row>
    </div>
  )
};

export default ShowTimesheet;