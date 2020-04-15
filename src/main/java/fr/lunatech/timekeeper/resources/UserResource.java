package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import fr.lunatech.timekeeper.resources.openapi.UserResourceApi;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

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
    public UserResponse me() {
        if (identity.getPrincipal() instanceof io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) {
            final var jwtCallerPrincipal = (io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) identity.getPrincipal();
            //final String name = identity.getPrincipal().getName();
            final String email = jwtCallerPrincipal.getClaims().getClaimValueAsString("email");
            //final String firstName = jwtCallerPrincipal.getClaims().getClaimValueAsString("given_name");
            //final String lastName = jwtCallerPrincipal.getClaims().getClaimValueAsString("family_name");
            return userService.findUserByEmail(email).orElseThrow(() -> new NotAuthorizedException("invalid_client"));
        } else {
            throw new NotAuthorizedException("invalid_token");
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
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
