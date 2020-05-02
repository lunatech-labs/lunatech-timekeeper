package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.resources.security.AuthenticatedUserInfo;
import fr.lunatech.timekeeper.services.dtos.OrganizationResponse;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.services.interfaces.OrganizationService;
import fr.lunatech.timekeeper.services.interfaces.ProjectService;
import io.quarkus.security.identity.SecurityIdentity;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static fr.lunatech.timekeeper.resources.security.SecurityIdentityUtils.retrieveAuthenticatedUserInfo;

public class ProjectResource implements ProjectResourceApi {

    @Inject
    ProjectService projectService;

    @Inject
    OrganizationService organizationService;

    @Inject
    SecurityIdentity identity;

    @RolesAllowed({"user", "admin"})
    @Override
    public List<ProjectResponse> getAllProjects() {
        //TODO delegate
        final AuthenticatedUserInfo authenticatedUserInfo = retrieveAuthenticatedUserInfo(identity);
        final Long organizationId = organizationService.findOrganizationByTokenName(authenticatedUserInfo.getOrganization())
                .map(OrganizationResponse::getId)
                .orElse(null);

        return projectService.listAllProjects(organizationId);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response createProject(@Valid ProjectRequest request, UriInfo uriInfo) {
        //TODO delegate
        final AuthenticatedUserInfo authenticatedUserInfo = retrieveAuthenticatedUserInfo(identity);
        final Long organizationId = organizationService.findOrganizationByTokenName(authenticatedUserInfo.getOrganization())
                .map(OrganizationResponse::getId)
                .orElse(null);

        final long projectId = projectService.createProject(organizationId, request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(projectId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public ProjectResponse getProject(Long id) {
        //TODO delegate
        final AuthenticatedUserInfo authenticatedUserInfo = retrieveAuthenticatedUserInfo(identity);
        final Long organizationId = organizationService.findOrganizationByTokenName(authenticatedUserInfo.getOrganization())
                .map(OrganizationResponse::getId)
                .orElse(null);

        return projectService.findProjectById(organizationId, id).orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProject(Long id, @Valid ProjectRequest request) {
        //TODO delegate
        final AuthenticatedUserInfo authenticatedUserInfo = retrieveAuthenticatedUserInfo(identity);
        final Long organizationId = organizationService.findOrganizationByTokenName(authenticatedUserInfo.getOrganization())
                .map(OrganizationResponse::getId)
                .orElse(null);

        projectService.updateProject(organizationId, id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
