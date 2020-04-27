package fr.lunatech.timekeeper.resources;

import com.sun.xml.bind.v2.TODO;
import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MembersUpdateRequest;
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
    public Response addMemberToProject(Long projectId, @Valid MemberRequest request, UriInfo uriInfo) {
        final Long memberId = projectService.addMemberToProject(projectId, request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(memberId.toString()).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProjectMembers(Long projectId, @Valid MembersUpdateRequest request, UriInfo uriInfo) {
        projectService.updateProjectMembers(projectId, request);
        return Response.noContent().build();
    }
}
