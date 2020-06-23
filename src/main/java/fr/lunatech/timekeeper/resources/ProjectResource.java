package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.ProjectResourceApi;
import fr.lunatech.timekeeper.resources.providers.AuthenticationContextProvider;
import fr.lunatech.timekeeper.services.ProjectService;
import fr.lunatech.timekeeper.services.TimeSheetService;
import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class ProjectResource implements ProjectResourceApi {
    private static Logger logger = LoggerFactory.getLogger(ProjectResource.class);

    @Inject
    ProjectService projectService;

    @Inject
    TimeSheetService timeSheetService;

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
    public Response getProject(Long id, Optional<Boolean> optimized,
                               @Context Request request,
                               @Context UriInfo ui) {
        final var ctx = authentication.context();
        ProjectResponse projectResponse = projectService.findResponseById(id, optimized, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));

        EntityTag etagValue = projectResponse.computeETag(ui.getRequestUri());
        Response.ResponseBuilder rb = request.evaluatePreconditions(etagValue);
        if(rb == null){
            logger.debug(String.format("Send project with ETag %s",etagValue));
            return Response
                    .status(Response.Status.OK)
                    .header("ETag",etagValue)
                    .entity(projectResponse)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();

        }else{
            logger.debug(String.format("Project %s not modified, return 304 Not Modified",etagValue));
            return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProject(Long id, @Valid ProjectRequest request) {
        final var ctx = authentication.context();
        projectService.update(id, request, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
        return Response.noContent().build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response joinPublicProject(Long projectId) {
        final var ctx = authentication.context();
        projectService.joinProject(projectId, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project or user not found for project id=%d and user id=%d", projectId, ctx.getUserId())));
        return Response.noContent().build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public TimeSheetResponse getLastActiveTimeSheetForUser(long idProject, long idUser) {
        final var ctx = authentication.context();
        Optional<TimeSheetResponse> maybeResponse = timeSheetService.findFirstForProjectForUser(idProject, idUser);
        return maybeResponse.orElseThrow(() -> new NotFoundException(String.format("No timesheet found for project_id=%d, and user_id=%d", idProject, idUser)));
    }
}
