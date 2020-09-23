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

import React from "react";
import {Collapse, List} from "antd";
import moment from 'moment';
import Space from "antd/lib/space";
import FolderFilled from "@ant-design/icons/lib/icons/FolderFilled";
import ProjectClientHeader from "../Projects/ProjectClientHeader";
import {CalendarOutlined, ClockCircleOutlined} from "@ant-design/icons";
import HistoryOutlined from "@ant-design/icons/lib/icons/HistoryOutlined";
import './UserTimeSheetList.less';

const {Panel} = Collapse;
const formatDate = (date) => {
    return  moment(date).format('DD-MM-YYYY');
};

const displayNumber = (number) => {
    if (number === 0) {
        return '0';
    }
    if (null === number){
        return '-';
    }
    return number.toString();
};

const now = moment();
export const makeItGrey = (now, startDate, expirationDate, daysLeft) => {
    const isNowBeforeStartDate = moment(now).isBefore(startDate);
    const isNowAfterEndDate = moment(now).isAfter(expirationDate);

    if (isNowBeforeStartDate || isNowAfterEndDate || daysLeft === 0 ) {
        return true;
    }
        return false;
};

const projectCard = (props) => {
    return (
        <div className="tk_CardCollapse">
            <Collapse bordered={false} expandIconPosition={'right'} key="timeSheets">
                <Panel header={<Space size="small"><FolderFilled/>{props.title}</Space>} key="1">
                    <List
                        className={'tk_TimeSheetList'}
                        dataSource={props.project}
                        renderItem={timeSheet => {
                            return (
                                <List.Item>
                                    <ProjectClientHeader project={timeSheet.project}
                                                         makeItGrey={makeItGrey(now, timeSheet.startDate, timeSheet.expirationDate, timeSheet.leftOver)}
                                    />
                                    <div
                                        className={makeItGrey(now, timeSheet.startDate, timeSheet.expirationDate, timeSheet.leftOver) ? 'tk_UnlimitedField' : ''}>
                                        <p>
                                            <ClockCircleOutlined/> TimeUnit: {timeSheet.timeUnit}</p>
                                        <p>
                                            <CalendarOutlined/> Number of days: <span>&nbsp;{displayNumber(timeSheet.maxDuration)}&nbsp;</span>
                                        </p>
                                        <p>
                                            <CalendarOutlined/> Start
                                            date: {timeSheet.startDate ? formatDate(timeSheet.startDate) : ' - '}
                                            <br/>
                                            <CalendarOutlined/> End
                                            date: {timeSheet.expirationDate ? formatDate(timeSheet.expirationDate) : ' - '}
                                        </p>
                                        <p>
                                            <HistoryOutlined/> Days left : <span>&nbsp;{displayNumber(timeSheet.leftOver)}&nbsp;</span>
                                        </p>
                                    </div>
                                </List.Item>

                            );
                            }
                        }
                    />
                </Panel>
            </Collapse>
        </div>
    );
}

export default projectCard;