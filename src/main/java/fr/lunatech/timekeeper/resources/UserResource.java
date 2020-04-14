package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.openapi.UserResourceApi;
import fr.lunatech.timekeeper.services.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/api/users")
public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity identity;

    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @NoCache
    public JwtUser me() {
        return new JwtUser(identity);
    }

    @Override
    public Response createUser(@Valid UserRequest request, UriInfo uriInfo) {
        final Long userId = userService.createUser(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(userId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public UserResponse getUser(Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateUser(Long id, @Valid UserRequest request) {
        userService.updateUser(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
