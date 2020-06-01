package fr.lunatech.timekeeper.resources.utils;

import fr.lunatech.timekeeper.services.requests.ClientRequest;
import fr.lunatech.timekeeper.services.requests.OrganizationRequest;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ClientResponse;
import fr.lunatech.timekeeper.services.responses.OrganizationResponse;
import fr.lunatech.timekeeper.services.responses.project.ProjectLightResponse;
import fr.lunatech.timekeeper.services.responses.project.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.UserResponse;

import static fr.lunatech.timekeeper.resources.utils.ResourceDefinition.*;

public class ResourceFactory {

    public static ProjectResponse create(ProjectRequest project, String token) {
        return InternalResourceUtils.createResource(project, ProjectDef.uri, ProjectResponse.class, token);
    }

    public static ProjectLightResponse read(Long id, String token) {
        return InternalResourceUtils.readResource(id, ProjectLightDef.uri, ProjectLightResponse.class, token);
    }

    public static ClientResponse create(ClientRequest client, String token) {
        return InternalResourceUtils.createResource(client, ClientDef.uri, ClientResponse.class, token);
    }

    public static UserResponse create(String token) {
        return InternalResourceUtils.readResource("me", UserDef.uri, UserResponse.class, token);
    }

    public static OrganizationResponse create(OrganizationRequest organization, String token) {
        return InternalResourceUtils.createResource(organization, OrganizationDef.uri, OrganizationResponse.class, token);
    }

    public static <P> void update(P request, String uri, String token) {
        InternalResourceUtils.updateResource(request, uri, token);
    }
}