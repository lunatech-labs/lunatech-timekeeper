import React from 'react';
import MainPage from "../MainPage/MainPage";
import UserList from "../../components/Users/UserList";

const UsersPage = () => {
    return (
        <MainPage title={"All users"}>
            <UserList/>
        </MainPage>
    )
};
export default UsersPage;