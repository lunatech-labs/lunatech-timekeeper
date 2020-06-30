import React from 'react';
import './EventMemberTag.less';
import PropTypes from 'prop-types';
import {Alert, Avatar, Spin} from 'antd';
import {useTimeKeeperAPI} from "../../utils/services";

const EventMemberPictures = ({membersIds}) => {
    const usersResponse = useTimeKeeperAPI('/api/users');

    if (usersResponse.loading) {
        return (
            <React.Fragment>
                <Spin size="large">
                    <p>Loading list of projects</p>
                </Spin>
            </React.Fragment>
        );
    }

    if (usersResponse.error) {
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

    const users = membersIds.map(memberId => usersResponse.data.filter(user => user.id === memberId))

    const displayAvatar = (users) => {
        return users.map(user => {
            return <Avatar key={`avatar-user-${user[0].id}`} src={user[0].picture} />
        })
    }

    return (
        displayAvatar(users)
    );
}
EventMemberPictures.propTypes = {
    membersIds: PropTypes.arrayOf(PropTypes.number).isRequired
};

export default EventMemberPictures;