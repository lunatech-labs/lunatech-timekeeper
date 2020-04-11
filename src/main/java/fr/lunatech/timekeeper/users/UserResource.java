package fr.lunatech.timekeeper.users;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/users")
public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public List<User> getAllUsers() {
        return userService.listAllUsers();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response createUser(User user) {
        return Response.ok(userService.insertUser(user)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public User getUser(@PathParam("id") Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response updateUser(@PathParam("id") Long id, User user) {
        return Response.ok(userService.updateUser(id, user).orElseThrow(NotFoundException::new)).build();
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response deleteUser(@PathParam("id") Long id) {
        return Response.ok(userService.deleteUser(id).orElseThrow(NotFoundException::new)).build();
    }

}
