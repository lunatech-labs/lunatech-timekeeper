package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.dtos.*;

import static fr.lunatech.timekeeper.resources.utils.RessourceDefinition.*;

public class ResourceFactory {

    public static ProjectResponse create(ProjectRequest project) {
        return AbstractResourceFactory.<ProjectResponse, ProjectRequest>createResource(project, ProjectDef.uri, ProjectResponse.class);
    }

    public static ClientResponse create(ClientRequest client) {
        return AbstractResourceFactory.<ClientResponse, ClientRequest>createResource(client, ClientDef.uri, ClientResponse.class);
    }

    public static UserResponse create(UserRequest user) {
        return AbstractResourceFactory.<UserResponse, UserRequest>createResource(user, UserDef.uri, UserResponse.class);
    }

    public static OrganizationResponse create(OrganizationRequest organization) {
        return AbstractResourceFactory.<OrganizationResponse, OrganizationRequest>createResource(organization, OrganizationDef.uri, OrganizationResponse.class);
    }

}
