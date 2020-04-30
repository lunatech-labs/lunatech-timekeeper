package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.services.dtos.RoleInProjectRequest;
import fr.lunatech.timekeeper.services.dtos.RoleInProjectUpdateRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.services.interfaces.ProjectService;

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

    @RolesAllowed({"user", "admin"})
    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectService.listAllProjects();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response createProject(@Valid ProjectRequest request, UriInfo uriInfo) {
        final long projectId = projectService.createProject(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(projectId)).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public ProjectResponse getProject(Long id) {
        return projectService.findProjectById(id).orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProject(Long id, @Valid ProjectRequest request) {
        projectService.updateProject(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response addRoleInProject(Long projectId, @Valid RoleInProjectRequest request, UriInfo uriInfo) {
        final Long roleInProjectId = projectService.addRoleInProjectToProject(projectId, request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(roleInProjectId.toString()).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateRolesInProjects(Long projectId, @Valid RoleInProjectUpdateRequest request, UriInfo uriInfo) {
        projectService.updateRolesInProjects(projectId, request);
        return Response.noContent().build();
    }
}
