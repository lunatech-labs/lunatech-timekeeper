import React from 'react';
import {Alert, Col, Divider, Row} from 'antd';
import TagProjectClient from '../Tag/TagProjectClient';
import './ShowTimeSheet.less';
import {DollarOutlined} from '@ant-design/icons';
import ClockCircleOutlined from '@ant-design/icons/lib/icons/ClockCircleOutlined';
import ProjectMemberTag from '../Projects/ProjectMemberTag';
import CalendarOutlined from '@ant-design/icons/lib/icons/CalendarOutlined';
import {useTimeKeeperAPI} from '../../utils/services';
import PropTypes from 'prop-types';
import CardMember from "../Card/CardMember";
const moment = require('moment');
const format = (s) => moment(s, 'YYYY-MM-DD').format('YYYY/MM/DD');
const ShowTimeSheet = ({project, member}) => {
  const {data, error, loading} = useTimeKeeperAPI(`/api/projects/${project.id}/users/${member.id}`);

  const TimeSheets = ({timeSheets}) => {
    if (timeSheets.size === 0) {
      return <div>No time sheets</div>;
    } else {
      return timeSheets.map(item =>
        <div key={`timesheet-information-${item.id}`}>
          <p>Edit</p>
          <Row gutter={32}>
            <Col span={12}>
              <p className="tk_information"><ClockCircleOutlined/> TimeUnit : {item.durationUnit}</p>
              <p className="tk_information"><CalendarOutlined/> End date
                : {item.expirationDate ? format(item.expirationDate) : '----/--/--'}</p>
            </Col>
            <Col span={12}>
              <p className="tk_information"><CalendarOutlined/> Number of days : {item.maxDuration}</p>
              <p className="tk_information"><DollarOutlined/> Billable : {item.defaultIsBillable ? 'Yes' : 'No'}</p>
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
      expirationDate: PropTypes.string,
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
      <CardMember><ProjectMemberTag member={member}/></CardMember>
      <Divider/>
      <TimeSheets timeSheets={data}/>
    </div>
  );
};

ShowTimeSheet.propTypes = {
  project: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    client: PropTypes.object
  }),
  member: PropTypes.object
};

export default ShowTimeSheet;