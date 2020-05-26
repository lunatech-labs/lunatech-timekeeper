package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.*;

import static fr.lunatech.timekeeper.resources.utils.TypeDefinition.apply;

public enum ResourceDefinition {

    ClientDef("/api/clients", apply(ClientRequest.class, ClientResponse.class)),
    OrganizationDef("/api/organizations", apply(OrganizationRequest.class, OrganizationResponse.class)),
    UserDef("/api/users", apply(Void.class, UserResponse.class)),
    ProjectDef("/api/projects", apply(ProjectRequest.class, ProjectResponse.class)),
    TimeSheetDef("/api/my/timeSheets", apply(Void.class, TimeSheetResponse.class));

    final public String uri;
    final public TypeDefinition typeDef;

    <R, T> ResourceDefinition(final String uri, TypeDefinition typeDef) {
        this.uri = uri;
        this.typeDef = typeDef;
    }

    public String uriWithid(Long id) {
        return String.format("%s/%s", this.uri, id);
    }

}
