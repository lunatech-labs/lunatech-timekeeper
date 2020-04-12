package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/users")
public interface UserResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(User user);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    User getUser(@PathParam("id") Long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<User> getAllUsers();

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUser(@PathParam("id") Long id, User user);

    @DELETE
    @Path("/{id}")
    Response deleteUser(@PathParam("id") Long id);
}
