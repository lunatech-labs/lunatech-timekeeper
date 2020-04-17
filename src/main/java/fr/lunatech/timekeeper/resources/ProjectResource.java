package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.services.interfaces.ProjectService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/api/projects")
public class ProjectResource implements ProjectResourceApi {

    @Inject
    ProjectService projectService;

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectService.listAllProjects();
    }

    @Override
    public Response createproject(@Valid ProjectRequest request, UriInfo uriInfo) {
        final long projectId = projectService.createProject(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(projectId)).build();
        return Response.created(uri).build();
    }

    @Override
    public ProjectResponse getproject(Long id) {
        return projectService.findProjectById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateproject(Long id, @Valid ProjectRequest request) {
        projectService.updateProject(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
