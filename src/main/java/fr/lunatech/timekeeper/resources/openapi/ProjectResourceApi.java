package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.requests.ProjectRequest;
import fr.lunatech.timekeeper.services.responses.ProjectResponse;
import fr.lunatech.timekeeper.services.responses.TimeSheetResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/projects")
public interface ProjectResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all projects",
            description = "Retrieve a project with the details and the list of project users and their respective roles.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Projects retrieved"
            )
    })
    List<ProjectResponse> getAllProjects();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new project",
            description = "Create a project with the details and the list of project users and their respective roles.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Project created",
                    headers = {
                            @Header(name = LOCATION, description = "New project url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createProject(@RequestBody ProjectRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve a project",
            description = " if optimized is false or is not precised: Retrieve a project with the details and the list of project users and their respective roles.\n" +
                    "else: Retrieve a project without list of users")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Project retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Project not found"
            )
    })
    ProjectResponse getProject(@PathParam("id") Long id, @QueryParam("optimized") Optional<Boolean> optimized);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a project",
            description = "Update a project with the details and the list of project users and their respective roles.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Project updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Project not found"
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Project cannot be updated by the current user"
            )
    })
    Response updateProject(@PathParam("id") Long id, @RequestBody ProjectRequest request);

    @PUT
    @Path("/{id}/join")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Join a project",
            description = "Join a public project")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Project updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Project not found"
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "The user cannot join the project"
            )
    })
    Response joinPublicProject(@PathParam("id") Long projectId);

    @GET
    @Path("/{idProject}/users/{idUser}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all timesheets of a member of a project",
            description = "Retrieve all timesheets of a member of a project with the details")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Timesheets retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Timesheets not found"
            )
    })
    TimeSheetResponse getLastActiveTimeSheetForUser(@PathParam("idProject") long idProject, @PathParam("idUser") long idUser);

}
