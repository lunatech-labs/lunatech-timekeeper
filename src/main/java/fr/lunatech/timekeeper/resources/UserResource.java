package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.openapi.UserResourceApi;
import fr.lunatech.timekeeper.services.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/users")
public class UserResource implements UserResourceApi {

    @Inject
    UserService userTkService;

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public JwtUser me() {
        return new JwtUser(identity);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(UserRequest request) {
        return Response.ok(userTkService.addUser(request)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public UserResponse readActivityById(@PathParam("id") long id) {
        return userTkService.getUserById(id).orElseThrow(NotFoundException::new);
    }

}
