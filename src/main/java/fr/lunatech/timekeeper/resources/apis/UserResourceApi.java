package fr.lunatech.timekeeper.resources.apis;

import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.dtos.UserUpdateRequest;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/users")
public interface UserResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<UserResponse> getAllUsers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(@RequestBody UserCreateRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse getUser(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUser(@PathParam("id") Long id, @RequestBody UserUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response deleteUser(@PathParam("id") Long id);
}
