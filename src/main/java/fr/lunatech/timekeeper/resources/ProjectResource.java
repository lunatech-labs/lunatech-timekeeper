package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.ProjectService;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class ProjectResource implements ProjectResourceApi {

    @Inject
    ProjectService projectService;

    @Inject
    AuthenticationContextProvider authentication;

    @RolesAllowed({"user", "admin"})
    @Override
    public List<ProjectResponse> getAllProjects() {
        final var ctx = authentication.context();
        return projectService.listAllResponses(ctx);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response createProject(@Valid ProjectRequest request, UriInfo uriInfo) {
        final var ctx = authentication.context();
        final long projectId = projectService.create(request, ctx);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(projectId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public ProjectResponse getProject(Long id) {
        final var ctx = authentication.context();
        return projectService.findResponseById(id, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProject(Long id, @Valid ProjectRequest request) {
        final var ctx = authentication.context();
        projectService.update(id, request, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
        return Response.noContent().build();
    }
}
