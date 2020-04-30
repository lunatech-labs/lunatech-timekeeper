package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
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

@Path("/users")
public interface UserResourceApi {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Extract current user from JWT",
            description = "Extract the current authenticated user from the JWT token.")
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
    @Operation(summary = "Retrieve all users",
            description = "Retrieve the list of users.")
    @Tag(ref = "users")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Users retrieved"
            )
    })
    List<UserResponse> getAllUsers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new user ",
            description = "Create an user with his profil. The email must be unique.")
    @Tag(ref = "users")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "User created",
                    headers = {
                            @Header(name = LOCATION, description = "New user url", schema = @Schema(type = SchemaType.STRING))
                    }
            )
    })
    Response createUser(@RequestBody UserRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve an user",
            description = "Retrieve user details.")
    @Tag(ref = "users")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "User retrieved"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    UserResponse getUser(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an user",
            description = "Update user details. Email can't be modified")
    @Tag(ref = "users")
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "User updated"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    Response updateUser(@PathParam("id") Long id, @RequestBody UserRequest request);

}
