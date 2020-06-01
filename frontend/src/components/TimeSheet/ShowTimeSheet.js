import React from 'react';
import {Alert, Col, Divider, Row} from "antd";
import TagProjectClient from "../Tag/TagProjectClient";
import './ShowTimeSheet.less';
import {DollarOutlined} from "@ant-design/icons";
import ClockCircleOutlined from '@ant-design/icons/lib/icons/ClockCircleOutlined';
import ProjectMemberTag from "../Projects/ProjectMemberTag";
import CalendarOutlined from "@ant-design/icons/lib/icons/CalendarOutlined";
import {useTimeKeeperAPI} from "../../utils/services";
import momentUtil from "../../utils/momentsUtil";
import PropTypes from 'prop-types';
const moment = momentUtil;
const ShowTimeSheet = ({project, member}) => {
  const {data, error, loading} = useTimeKeeperAPI(`/projects/${project.id}/user/${member.id}/timesheets`);

  const TimeSheets = ({timeSheets}) => {
    if (timeSheets.size === 0) {
      return <div>No time sheets</div>
    } else {
      return timeSheets.map(item =>
        <div>
          <p>Edit</p>
          <Row gutter={32}>
            <Col span={12}>
              <p className="tk_information"><ClockCircleOutlined/> TimeUnit : {item.durationUnit}</p>
              <p className="tk_information"><CalendarOutlined/> End date
                : {moment(item.expirationDate).format('yyyy/MM/dd')}</p>
            </Col>
            <Col span={12}>
              <p className="tk_information"><CalendarOutlined/> Number of days : {item.maxDuration}</p>
              <p className="tk_information"><DollarOutlined/> Billable : {item.defaultIsBillable}</p>
            </Col>
          </Row>
        </div>
      );
    }
  };

  TimeSheets.propTypes = {
    timeSheets: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number.isRequired,
      defaultIsBillable: PropTypes.bool.isRequired,
      expirationDate: PropTypes.object,
      maxDuration: PropTypes.number,
      durationUnit: PropTypes.string
    }))
  };

  if (error) {
    let errorReason = 'Message: ' + error;
    return (
      <React.Fragment>
        <Alert title='Server error'
               message='Failed to load time sheets from Quarkus backend server'
               type='error'
               description={errorReason}
        />
      </React.Fragment>
    );
  }
  if (loading) {
    return (
      <div>loading...</div>
    );
  }

  return (
    <div>
      <div><h1>Individual time sheet</h1></div>
      <h2>{project.name}</h2><TagProjectClient client={project.client}/>
      <div><ProjectMemberTag member={member}/></div>
      <Divider/>
      <TimeSheets timeSheets={data}/>
    </div>
  );
};


export default ShowTimeSheet;