/*
 * Copyright 2020 Lunatech Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {createContext} from 'react';
import PropTypes from 'prop-types';

export const UserContext = createContext(null);

export const UserProvider = ({currentUser, children}) => {
  return (
    <UserContext.Provider value={{currentUser}}>
      {children}
    </UserContext.Provider>
  );
};

UserProvider.propTypes = {
  currentUser: PropTypes.shape({
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