import React, {createContext} from 'react';

export const UserContext = createContext(null);

export const UserProvider = ({user, children}) => {
  return (
    <UserContext.Provider value={ {user} }>
      {children}
    </UserContext.Provider>
  );
};