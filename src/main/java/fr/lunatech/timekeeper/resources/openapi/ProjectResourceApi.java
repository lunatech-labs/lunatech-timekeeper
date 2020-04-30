package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.RoleInProjectRequest;
import fr.lunatech.timekeeper.services.dtos.RoleInProjectUpdateRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
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

import static javax.ws.rs.core.HttpHeaders.LOCATION;

@Path("/projects")
public interface ProjectResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve all projects",
            description = "Retrieve the list of projects.")
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
    @Operation(summary = "Create a new project ",
            description = "Create a project.")
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
            description = "Retrieve project details.")
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
    ProjectResponse getProject(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a project",
            description = "Update project details.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Project updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Project not found"
            )
    })
    Response updateProject(@PathParam("id") Long id, @RequestBody ProjectRequest request);

    @POST
    @Path("{id}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a user with a role to a project",
            description = "Add a user to a project and determine his role.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "RoleInProject created",
                    headers = {
                            @Header(name = LOCATION, description = "New roleInProject url", schema = @Schema(type = SchemaType.STRING))
                    }
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad parameters (unknown project or user)"
            )
    })
    Response addRoleInProject(@PathParam("id") Long projectId, @RequestBody RoleInProjectRequest request, @Context UriInfo uriInfo);

    @PUT
    @Path("{id}/members")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update rolesInProjects",
            description = "Update the list of roleInProject.")
    @Tag(ref = "projects")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "roleInProject updated"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad parameters (unknown project or user)"
            )
    })
    Response updateRolesInProjects(@PathParam("id") Long projectId, @RequestBody RoleInProjectUpdateRequest request, @Context UriInfo uriInfo);

}
