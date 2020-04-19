import React from 'react';
import ShowUserPage from './ShowUserPage.jsx';
import MainPage from '../MainPage/MainPage';

const HomePage = () => {
  return (
    <MainPage>
      <ShowUserPage/>
    </MainPage>
  );
};

export default HomePage;