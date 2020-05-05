package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.responses.UserResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/users")
public interface UserResourceApi {

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Retrieve the current user",
            description = "Retrieve the current authenticated user from the JWT token.")
    @Tag(ref = "users")
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

}
