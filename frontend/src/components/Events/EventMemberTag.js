import React from 'react';
import './EventMemberTag.less';
import PropTypes from 'prop-types';
import {Alert, Avatar, Spin} from 'antd';
import TagMember from '../Tag/TagMember';
import CardXs from '../Card/CardXs';
import CardLg from "../Card/CardLg";
import {useTimeKeeperAPI} from "../../utils/services";

const EventMemberTag = ({ member }) => {
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

    const user = usersResponse.data.filter(user => user.id === member.id);
    return (
        <CardXs>
            <div>
                <Avatar src={user[0].picture}/>
                <p>{user[0].name}</p>
            </div>
        </CardXs>
    );
};

EventMemberTag.propTypes = {
    member: PropTypes.shape({
        name: PropTypes.string.isRequired,
        picture: PropTypes.string
    })
};

export default EventMemberTag;