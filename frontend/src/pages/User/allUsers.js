import React from 'react';
import MainPage from 'pages/MainPage/MainPage';
import UserList from 'components/Users/UserList';

const UsersPage = () => {
  return (
    <MainPage title={'List of users'}>
      <UserList/>
    </MainPage>
  );
};
export default UsersPage;