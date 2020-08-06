import React from "react";
import moment from "moment";
import {computeNumberOfHours} from "../../utils/momentUtils";
import {Badge} from "antd";
import './UserEvent.less'
import PropTypes from "prop-types";

const computeSize = (nbHours) => {
    const minimumSize = 75;
    return minimumSize + (nbHours - 1) * 50;
};
const UserEvent = ({userEvent}) => {
    if(userEvent){
        const start = moment(userEvent.startDateTime).utc();
        const end = moment(userEvent.endDateTime).utc();
        const date = start.clone();
        const hours = computeNumberOfHours(start, end);
        date.set({
            hour: hours
        });
        const size = computeSize(hours);
        return (
            <div className="tk_UserEvent" style={{height: `${size}px`}}
                 key={`badge-entry-${start && start.format('yyyy-mm-dd-hh-mm')}`}>
                <div>
                    <Badge
                        status={(userEvent && userEvent.name) ? 'success' : 'error'}
                        text={(userEvent && userEvent.name) ? `${userEvent.name}` : 'Nothing to render'}
                    />
                    <p>{(userEvent && userEvent.description && userEvent.description) ? userEvent.description : ''}</p>
                </div>
                <p>{moment(userEvent.startDateTime).format('hh:mm')} - {moment(userEvent.endDateTime).format('hh:mm')}</p>
            </div>
        );
    }
    return '';
}
UserEvent.propTypes = {
    userEvent: PropTypes.arrayOf(
        PropTypes.shape({
            id: PropTypes.number,
            date: PropTypes.string,
            name: PropTypes.string,
            description: PropTypes.string,
            eventType: PropTypes.string,
            startDateTime: PropTypes.string,
            endDateTime: PropTypes.string,
            duration: PropTypes.string
        })
    ).isRequired
};

export default UserEvent;