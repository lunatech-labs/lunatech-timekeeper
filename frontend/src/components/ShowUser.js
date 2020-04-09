import React, { Component } from 'react';

class ShowUser extends Component {
    render() {
        return (
            <div className='userProfile'>
                <p>Name: {this.props.user.name}</p>
                <p>First name: {this.props.user.givenName}</p>
                <p>Last name: {this.props.user.familyName}</p>
                <p>Email: {this.props.user.email}</p>
            </div>
    );
    }

}
export default ShowUser


