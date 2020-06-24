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
    public Response getProject(Long id,
                               Optional<Boolean> optimized,
                               @Context Request request) {
        final var ctx = authentication.context();
        ProjectResponse projectResponse = projectService.findResponseById(id, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));

        EntityTag etagValue = projectResponse.computeETag();
        Response.ResponseBuilder rb = request.evaluatePreconditions(etagValue);
        if(rb == null){
            //If rb is null then either it is first time request; or resource is modified
            //Get the updated representation and return with Etag attached to it
            logger.debug(String.format("Send project with ETag [%s]",etagValue));

            if(optimized.orElse(false)){
                ProjectResponse projectResponseWithoutUsers = ProjectResponse.copyWithoutUsers(projectResponse);
                return Response
                        .status(Response.Status.OK)
                        .tag(etagValue)
                        .entity(projectResponseWithoutUsers)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }else{
                return Response
                        .status(Response.Status.OK)
                        .tag(etagValue)
                        .entity(projectResponse)
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build();
            }
        }else{
            logger.debug(String.format("Project %s not modified, return 304 Not Modified",etagValue));
            return rb.build();
        }
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public Response updateProject(Long id,
                                  @Valid ProjectRequest projectRequest,
                                  @Context Request request
    ) {
        final var ctx = authentication.context();

        ProjectResponse projectResponse = projectService.findResponseById(id, ctx)
                .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));

        EntityTag etagValue = projectResponse.computeETag();
        Response.ResponseBuilder rb = request.evaluatePreconditions(etagValue);
        // If the  client send a If-Match with the same Etag OR the client DOES NOT send any If-Match, then just save the entity
        if (rb == null) {
            projectService.update(id, projectRequest, ctx)
                    .orElseThrow(() -> new NotFoundException(String.format("Project not found for id=%d", id)));
            return Response.noContent().build();
        } else {
            // The client send a If-Match with a ETag, which does not match the ETag we computed -> thus we return a 412 HTTP Error
            return Response
                    .status(412)
                    .entity("{\"message\":\"If-Match HTTP Header is missing or the Project was updated. Cannot update this version of the Project.\"}").build();
        }
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
