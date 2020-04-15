package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
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

@Path("/api/users")
public interface UserResourceApi {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Extract current user from JWT",
            description = "Extract the current authenticated user from the JWT token. Endpoint required for the React application.")
    @Tag(ref = "security")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "User retrieved"
            ),
            @APIResponse(
                    responseCode = "403",
                    description = "Invalid JWT token")
    })
    UserResponse me();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserResponse> getAllUsers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(@RequestBody UserRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse getUser(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUser(@PathParam("id") Long id, @RequestBody UserRequest request);

}
