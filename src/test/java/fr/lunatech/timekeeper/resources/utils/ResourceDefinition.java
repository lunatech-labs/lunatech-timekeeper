package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.dtos.*;

import static fr.lunatech.timekeeper.resources.utils.TypeDefinition.apply;

public enum ResourceDefinition {

    ClientDef("/api/clients", apply(ClientRequest.class, ClientResponse.class)),
    OrganizationDef("/api/organizations", apply(OrganizationRequest.class, OrganizationResponse.class)),
    UserDef("/api/users", apply(UserRequest.class, UserResponse.class)),
    ProjectDef("/api/projects", apply(ProjectRequest.class, ProjectResponse.class));

    final public String uri;
    final public TypeDefinition typeDef;

    <R, T> ResourceDefinition(final String uri, TypeDefinition typeDef) {
        this.uri = uri;
        this.typeDef = typeDef;
    }
}
