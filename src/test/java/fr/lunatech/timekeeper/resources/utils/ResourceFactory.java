package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.dtos.*;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;

public class ResourceFactory {

    public static ProjectResponse create(ProjectRequest project, String token) {
        return InternalResourceUtils.<ProjectResponse, ProjectRequest>createResource(project, ProjectDef.uri, ProjectResponse.class, token);
    }

    public static ClientResponse create(ClientRequest client, String token) {
        return InternalResourceUtils.<ClientResponse, ClientRequest>createResource(client, ClientDef.uri, ClientResponse.class, token);
    }

    public static UserResponse create(UserRequest user, String token) {
        return InternalResourceUtils.<UserResponse, UserRequest>createResource(user, UserDef.uri, UserResponse.class, token);
    }

    public static OrganizationResponse create(OrganizationRequest organization, String token) {
        return InternalResourceUtils.<OrganizationResponse, OrganizationRequest>createResource(organization, OrganizationDef.uri, OrganizationResponse.class, token);
    }

    public static <P> void update(P request, String uri, String token) {
        InternalResourceUtils.updateResource(request, uri, token);
    }
}