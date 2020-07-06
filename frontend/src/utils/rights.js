

export const canEditProject = (project, currentUser, keycloak) => isTeamLeader(project, currentUser) || isAdmin(keycloak);

export const isTeamLeader = (project, currentUser) => {
  const member = project.users.find(user => currentUser.id === user.id);
  return member && member.manager;
};

export const isAdmin = (keycloak) => {
  return keycloak.hasRealmRole('admin');
};

export const isUser = (keycloak) => {
  return keycloak.hasRealmRole('user');
};

