import React from 'react';
import {useTimeKeeperAPI} from "../../utils/services";
import {Alert, Card, Collapse, List, Space, Spin} from "antd";
import UserOutlined from "@ant-design/icons/lib/icons/UserOutlined";
import CalendarOutlined from "@ant-design/icons/lib/icons/CalendarOutlined";
import EventMemberTag from "./EventMemberTag";
import PropTypes from "prop-types";
import './EventsList.less';
import moment from "moment";

const {Panel} = Collapse;

const EventsList = () => {
    const eventsResponse = useTimeKeeperAPI('/api/events');

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
                    <div className="tk_EventCard_Body">
                    <p className="tk_CardEvent_Desc">{item.description}</p>
                    <div className="tk_CardEvent_Bottom">
                        <div className="tk_CardEvent_Date">
                            <CalendarOutlined />
                            <p>{moment(item.startDateTime, 'YYYY-MM-DD-HH:mm:ss.SSS\'Z\'').utc().format('LLL')}<br />{moment(item.endDateTime, 'YYYY-MM-DD-HH:mm:ss.SSS\'Z\'').utc().format('LLL')}</p>
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
                    </div>
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