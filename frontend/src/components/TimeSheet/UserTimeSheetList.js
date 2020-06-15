import React from 'react';
import PropTypes from 'prop-types';
import Space from "antd/lib/space";
import {Avatar, Collapse, List} from 'antd';
import FolderFilled from "@ant-design/icons/lib/icons/FolderFilled";
import logo from "../../img/logo_timekeeper_homepage.png";
import TagProjectClient from "../Tag/TagProjectClient";
import {CalendarOutlined, ClockCircleOutlined} from "@ant-design/icons";
import moment from 'moment'
import HistoryOutlined from "@ant-design/icons/lib/icons/HistoryOutlined";


const {Panel} = Collapse;
const format = (s) => moment(s, 'YYYY-MM-DD').format('YYYY/MM/DD');

const UserTimeSheetList = ({timeSheets}) => {
    return (
        <Collapse bordered={false} expandIconPosition={'right'} key="timeSheets">
            <Panel header={<Space size="small"><FolderFilled/>{'Time sheets list'}</Space>} key="1">
                <List
                    className={'tk_Project_MemberList'}
                    dataSource={timeSheets}
                    renderItem={timeSheet => {
                        if (!timeSheet.project.publicAccess)
                            return (
                                <List.Item>
                                    <Avatar src={logo} shape={'square'} size="large"/>
                                    <div>{timeSheet.project.name}</div>
                                    <TagProjectClient client={timeSheet.project.client}/>
                                    <p><ClockCircleOutlined/> TimeUnit: {timeSheet.timeUnit}</p>
                                    <p><CalendarOutlined/> Number of days: {timeSheet.maxDuration ? timeSheet.maxDuration : "Unlimited"}</p>
                                    <p><CalendarOutlined/> End date: {timeSheet.expirationDate ? format(timeSheet.expirationDate) : 'Unlimited'}</p>
                                    <p><HistoryOutlined/>Days left : {timeSheet.leftOver ? timeSheet.leftOver : 'Unlimited'}</p>
                                </List.Item>
                            )
                    }}
                />
            </Panel>
        </Collapse>

    );
}

UserTimeSheetList.propTypes = {
    timeSheets: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number.isRequired,
            project: PropTypes.shape({
                name: PropTypes.string,
                publicAccess: PropTypes.bool,
                client: PropTypes.object
            }),
            defaultIsBillable: PropTypes.bool.isRequired,
            expirationDate: PropTypes.string,
            maxDuration: PropTypes.number,
            durationUnit: PropTypes.string,
            leftOver: PropTypes.number
        })).isRequired
};

export default UserTimeSheetList;