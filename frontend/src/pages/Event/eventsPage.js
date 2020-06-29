import React from 'react';
import MainPage from '../MainPage/MainPage';
import EventsList from "../../components/Events/EventsList";


const EventsPage = () => {
  return (
    <MainPage title="Events">
        <EventsList />
    </MainPage>
  );
};

export default EventsPage;

