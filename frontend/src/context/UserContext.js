import React, {createContext} from 'react';
import PropTypes from 'prop-types';

export const UserContext = createContext(null);

export const UserProvider = ({user, children}) => {
  return (
    <UserContext.Provider value={{user}}>
      {children}
    </UserContext.Provider>
  );
};

UserProvider.propTypes = {
  user: PropTypes.shape({
    id: PropTypes.number.isRequired,
    name: PropTypes.string.isRequired,
    email: PropTypes.string.isRequired,
    picture: PropTypes.string,
    profiles: PropTypes.arrayOf(PropTypes.string).isRequired,
    projects: PropTypes.arrayOf(PropTypes.shape({
      id: PropTypes.number.isRequired,
      manager: PropTypes.bool.isRequired,
      name: PropTypes.string.isRequired,
      publicAccess: PropTypes.bool.isRequired
    })),
  }),
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node
  ]).isRequired
};