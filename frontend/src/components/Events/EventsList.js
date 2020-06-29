import React, {useState} from 'react';
import {useTimeKeeperAPI} from "../../utils/services";
import {Alert, Avatar, Card, Collapse, List, Space, Spin} from "antd";
import UserOutlined from "@ant-design/icons/lib/icons/UserOutlined";
import CalendarOutlined from "@ant-design/icons/lib/icons/CalendarOutlined";
import EventMemberTag from "./EventMemberTag";
import PropTypes from "prop-types";
import './EventsList.less';
import logo from "../../img/logo_timekeeper_homepage.png";
import UnlockOutlined from "@ant-design/icons/lib/icons/UnlockOutlined";
import LockFilled from "@ant-design/icons/lib/icons/LockFilled";

const {Panel} = Collapse;

const EventsList = () => {
    const eventsResponse = useTimeKeeperAPI('/api/events');
    const [startMonth, setStartMonth] = useState();
    const [startHour, setStartHour] = useState();
    const [startMinute, setStartMinute] = useState();
    const [endMonth, setEndMonth] = useState();
    const [endHour, setEndHour] = useState();
    const [endMinute, setEndMinute] = useState();

    if (eventsResponse.loading) {
        return (
            <React.Fragment>
                <Spin size="large">
                    <p>Loading list of projects</p>
                </Spin>
            </React.Fragment>
        );
    }

    if (eventsResponse.error) {
        return (
            <React.Fragment>
                <Alert title='Server error'
                       message='Failed to load the list of projects'
                       type='error'
                       description='Unable to fetch the list of Projects from the server'
                />
            </React.Fragment>
        );
    }

    const computeStartDate = (date) => {
        switch (date[1]) {
            case 1:
                setStartMonth("January");
                break;
            case 2:
                setStartMonth("February");
                break;
            case 3:
                setStartMonth("March");
                break;
            case 4:
                setStartMonth("April");
                break;
            case 5:
                setStartMonth("May");
                break;
            case 6:
                setStartMonth("June");
                break;
            case 7:
                setStartMonth("July");
                break;
            case 8:
                setStartMonth("August");
                break;
            case 9:
                setStartMonth("September");
                break;
            case 10:
                setStartMonth("October");
                break;
            case 11:
                setStartMonth("November");
                break;
            case 12:
                setStartMonth("December");
                break;
            default:
                break;
        }

        switch (date[3]) {
            case 0:
                setStartHour("00");
                break;
            case 1:
                setStartHour("01");
                break;
            case 2:
                setStartHour("02");
                break;
            case 3:
                setStartHour("03");
                break;
            case 4:
                setStartHour("04");
                break;
            case 5:
                setStartHour("05");
                break;
            case 6:
                setStartHour("06");
                break;
            case 7:
                setStartHour("07");
                break;
            case 8:
                setStartHour("08");
                break;
            case 9:
                setStartHour("09");
                break;
            default:
                setStartHour(date[3]);
        }
        switch (date[4]) {
            case 0:
                setStartMinute("00");
                break;
            case 1:
                setStartMinute("01");
                break;
            case 2:
                setStartMinute("02");
                break;
            case 3:
                setStartMinute("03");
                break;
            case 4:
                setStartMinute("04");
                break;
            case 5:
                setStartMinute("05");
                break;
            case 6:
                setStartMinute("06");
                break;
            case 7:
                setStartMinute("07");
                break;
            case 8:
                setStartMinute("08");
                break;
            case 9:
                setStartMinute("09");
                break;
            default:
                setStartHour(date[4]);
        }
        return startMonth + " " + date[2] + ", " + date[0] + " - " + startHour + ":" + startMinute;
    }

    const computeEndDate = (date) => {
        switch (date[1]) {
            case 1:
                setEndMonth("January");
                break;
            case 2:
                setEndMonth("February");
                break;
            case 3:
                setEndMonth("March");
                break;
            case 4:
                setEndMonth("April");
                break;
            case 5:
                setEndMonth("May");
                break;
            case 6:
                setEndMonth("June");
                break;
            case 7:
                setEndMonth("July");
                break;
            case 8:
                setEndMonth("August");
                break;
            case 9:
                setEndMonth("September");
                break;
            case 10:
                setEndMonth("October");
                break;
            case 11:
                setEndMonth("November");
                break;
            case 12:
                setEndMonth("December");
                break;
            default:
                break;
        }

        switch (date[3]) {
            case 0:
                setEndHour("00");
                break;
            case 1:
                setEndHour("01");
                break;
            case 2:
                setEndHour("02");
                break;
            case 3:
                setEndHour("03");
                break;
            case 4:
                setEndHour("04");
                break;
            case 5:
                setEndHour("05");
                break;
            case 6:
                setEndHour("06");
                break;
            case 7:
                setEndHour("07");
                break;
            case 8:
                setEndHour("08");
                break;
            case 9:
                setEndHour("09");
                break;
            default:
                setEndHour(date[3]);
        }
        switch (date[4]) {
            case 0:
                setEndMinute("00");
                break;
            case 1:
                setEndMinute("01");
                break;
            case 2:
                setEndMinute("02");
                break;
            case 3:
                setEndMinute("03");
                break;
            case 4:
                setEndMinute("04");
                break;
            case 5:
                setEndMinute("05");
                break;
            case 6:
                setEndMinute("06");
                break;
            case 7:
                setEndMinute("07");
                break;
            case 8:
                setEndMinute("08");
                break;
            case 9:
                setEndMinute("09");
                break;
            default:
                setEndMinute(date[4]);
        }
        return endMonth + " " + date[2] + ", " + date[0] + " - " + endHour + ":" + endMinute;
    }

    const DataList = ({data}) => <List
        id="tk_List"
        grid={{gutter: 32, column: 3}}
        dataSource={data}
        renderItem={item => (
            <List.Item key={item.id}>
                <Card
                    id="tk_CardEvent"
                    bordered={false}
                    title={item.name}
                >
                    <p className="tk_CardEvent_Desc">{item.description}</p>
                    <div className="tk_CardEvent_Bottom">
                        <div className="tk_CardEvent_Date">
                            <CalendarOutlined />
                            <p>{computeStartDate(item.startDateTime)}<br />{computeEndDate(item.endDateTime)}</p>
                        </div>
                        <div className="tk_CardEvent_People">
                            <div>
                                <img src="https://opencollective.com/debug/backer/10/avatar.svg" />
                                <img src="https://opencollective.com/debug/backer/10/avatar.svg" />
                                <img src="https://opencollective.com/debug/backer/10/avatar.svg" />
                            </div>
                            <a href="#">32 people</a>
                        </div>
                    </div>










                    <Collapse bordered={false} expandIconPosition={'right'} key="projects">
                        <Panel header={<Space
                                size="small"><UserOutlined/>{item.attendees.length}{" people"}</Space>}
                               key="members">
                            <List
                                id={'tk_ProjectMembers'}
                                dataSource={item.attendees}
                                renderItem={member => (
                                    <List.Item>
                                        <EventMemberTag member={member}/>
                                    </List.Item>
                                )}
                            />
                        </Panel>
                    </Collapse>
                </Card>
            </List.Item>
        )}
    />;
    DataList.propTypes = {
        data: PropTypes.array
    };

    return(
        DataList(eventsResponse)
    );
}
export default EventsList;