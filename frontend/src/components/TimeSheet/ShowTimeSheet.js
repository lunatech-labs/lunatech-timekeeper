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

import React, {useState} from 'react';
import {Alert, Button, Col, Row} from 'antd';
import './ShowTimeSheet.less';
import '../Modal/ModalGeneral.less';
import {DollarOutlined, ClockCircleOutlined, CalendarOutlined, FieldTimeOutlined} from '@ant-design/icons';
import ProjectMemberTag from '../Projects/ProjectMemberTag';
import {useTimeKeeperAPI} from '../../utils/services';
import PropTypes from 'prop-types';
import EditTimeSheetForm from './EditTimeSheetForm';
import ProjectClientHeader from '../Projects/ProjectClientHeader';

const moment = require('moment');
const format = (s) => moment(s, 'YYYY-MM-DD').format('DD/MM/YYYY');
const ShowTimeSheet = ({project, member}) => {
  const {data, error, loading} = useTimeKeeperAPI(`/api/projects/${project.id}/users/${member.id}`);
  const [selectedTimeSheet, setSelectedTimeSheet] = useState();

  const TimeSheet = ({timeSheet}) => {
    return (
      <div className="tk_ModalGen">
        <div className="tk_ModalTop">
          <div className="tk_ModalTopHead">
            <h1>Individual timesheet</h1>
            <Button type="link" onClick={() => {setSelectedTimeSheet(timeSheet);}}>Edit</Button>
          </div>
          <div className="tk_ModalTopBody">
            <div className="tk_ModalTopProject">
              <ProjectClientHeader project={project}/>
              <p><FieldTimeOutlined/>Days left: {timeSheet.leftOver ? timeSheet.leftOver : 'Unlimited'}</p>
            </div>
            <ProjectMemberTag member={member}/>
            <Row gutter={32}>
              <Col span={12}>
                <p className="tk_Information"><ClockCircleOutlined/> TimeUnit: {timeSheet.timeUnit}</p>
                <p className="tk_Information"><CalendarOutlined/> Start
                  date: {timeSheet.startDate ? format(timeSheet.startDate) : '--/--/----'}</p>
                <p className="tk_Information"><CalendarOutlined/> End
                  date: {timeSheet.expirationDate ? format(timeSheet.expirationDate) : '--/--/----'}</p>
              </Col>
              <Col span={12}>
                <p className="tk_Information"><CalendarOutlined/> Number of days: {timeSheet.maxDuration}
                </p>
                <p className="tk_Information">
                  <DollarOutlined/> Billable: {timeSheet.defaultIsBillable ? 'Yes' : 'No'}</p>
              </Col>
            </Row>
            <div key={`timesheet-information-${timeSheet.id}`}>
            </div>
          </div>
        </div>

        <div className="tk_ModalBottom">
          {selectedTimeSheet && <EditTimeSheetForm timeSheet={selectedTimeSheet}/>}
        </div>
      </div>
    );
  };

  TimeSheet.propTypes = {
    timeSheet: PropTypes.shape({
      id: PropTypes.number.isRequired,
      defaultIsBillable: PropTypes.bool.isRequired,
      expirationDate: PropTypes.string,
      maxDuration: PropTypes.number,
      durationUnit: PropTypes.string,
      timeUnit: PropTypes.string,
      leftOver: PropTypes.number,
      startDate: PropTypes.string
    })
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
    <TimeSheet timeSheet={data}/>
  );
};

ShowTimeSheet.propTypes = {
  project: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    client: PropTypes.object,
  }),
  member: PropTypes.object
};

export default ShowTimeSheet;