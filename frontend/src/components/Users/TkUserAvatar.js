import React from 'react';
import PropTypes from 'prop-types';
import {Avatar} from 'antd';
import UserOutlined from "@ant-design/icons/lib/icons/UserOutlined";


const genNameColor = (name) => '#' + intToRGB(hashCode(name));
const hashCode = (str) => { // java String#hashCode
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
}
const intToRGB = (i) =>{
    const c = (i & 0x00FFFFFF)
        .toString(16)
        .toUpperCase();
    return "00000".substring(0, 6 - c.length) + c;
}

const TkUserAvatar = ({ name,picture }) => {
    if (picture){
        // return given picture in Avatar
        return <Avatar src={picture}/>
    }else if (name){
        // return first letter of the name in Avatar (google style with background color based on name value)
        return <Avatar className={"tk_user_avatar"} style={{
            backgroundColor: genNameColor(name),
            fontSize: "large",
            fontFamily:'HelveticaNeue-Light,Helvetica Neue Light,Helvetica Neue,Helvetica, Arial,Lucida Grande, sans-serif'
        }}>{name.substr(0, 1)}</Avatar>
    }else{
        // return an icon in Avatar
        return <Avatar icon={<UserOutlined />}/>
    }
};

TkUserAvatar.propTypes = {
    name: PropTypes.string,
    picture: PropTypes.string
};

export default TkUserAvatar;