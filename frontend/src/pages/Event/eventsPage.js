import React from 'react';
import MainPage from '../MainPage/MainPage';
import EventsList from '../../components/Events/EventsList';
import {useKeycloak} from '@react-keycloak/web';
import {Link} from 'react-router-dom';


const EventsPage = () => {
  const [keycloak] = useKeycloak();
  const isAdmin = keycloak.hasRealmRole('admin');
  const actions = isAdmin && <Link id="btnAddEvent" className="tk_Btn tk_BtnPrimary" key='addLink' to={'#'}>Add event</Link>;
  return (
    <MainPage
      title="Events"
      actions={actions}
    >
      <EventsList />
    </MainPage>
  );
};

export default EventsPage;

