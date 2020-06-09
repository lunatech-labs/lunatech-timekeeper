import React, {useState} from 'react';
import {Alert, Col, Divider, Row} from 'antd';
import TagProjectClient from '../Tag/TagProjectClient';
import './ShowTimeSheet.less';
import {
    DollarOutlined,
    ClockCircleOutlined,
    CalendarOutlined,
    FieldTimeOutlined,
} from '@ant-design/icons';
import ProjectMemberTag from '../Projects/ProjectMemberTag';
import {useTimeKeeperAPI} from '../../utils/services';
import PropTypes from 'prop-types';
import EditTimeSheetForm from "./EditTimeSheetForm";

const moment = require('moment');
const format = (s) => moment(s, 'YYYY-MM-DD').format('YYYY/MM/DD');
const ShowTimeSheet = ({project, member}) => {
    const {data, error, loading} = useTimeKeeperAPI(`/api/projects/${project.id}/users/${member.id}`);
    const [selectedTimeSheet, setSelectedTimeSheet] = useState()

    const TimeSheet = ({timeSheet}) => {

        return (
            <div key={`timesheet-information-${timeSheet.id}`}>
                <Row gutter={32}>
                    <Col span={12}>
                        <p className="tk_Information"><ClockCircleOutlined/> TimeUnit: {timeSheet.durationUnit}</p>
                        <p className="tk_Information"><CalendarOutlined/> End
                            date: {timeSheet.expirationDate ? format(timeSheet.expirationDate) : '----/--/--'}</p>
                    </Col>
                    <Col span={12}>
                        <p className="tk_Information"><CalendarOutlined/> Number of days: {timeSheet.maxDuration}</p>
                        <p className="tk_Information">
                            <DollarOutlined/> Billable: {timeSheet.defaultIsBillable ? 'Yes' : 'No'}</p>
                    </Col>
                </Row>
                {/* Move this outside  show part in top, edit part in bottom*/}
                <a onClick={() => {
                    setSelectedTimeSheet(timeSheet);
                }}>Edit timesheet</a>
                {selectedTimeSheet && <EditTimeSheetForm timesheet={selectedTimeSheet}/>}
            </div>);
    };

    TimeSheet.propTypes = {
        timeSheet: PropTypes.shape({
            id: PropTypes.number.isRequired,
            defaultIsBillable: PropTypes.bool.isRequired,
            expirationDate: PropTypes.string,
            maxDuration: PropTypes.number,
            durationUnit: PropTypes.string
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
        <div>
            <div className="tk_ModalTop">
                <div className="tk_ModalTopHead">
                    <h1>Individual time sheet</h1>
                </div>
                <div className="tk_ModalTopBody">
                    <div className="tk_ModalTopProject">
                        <div>
                            <h2>{project.name}</h2>
                            <Divider type='vertical'/>
                            <TagProjectClient client={project.client}/>
                        </div>
                        <p><FieldTimeOutlined/>Days left: (TO DO)</p>
                    </div>
                    <ProjectMemberTag member={member}/>
                </div>
            </div>
            <div className="tk_ModalBottom">
                <TimeSheet timeSheet={data}/>
            </div>
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