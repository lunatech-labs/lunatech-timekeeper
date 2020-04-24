package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Profile;
import fr.lunatech.timekeeper.resources.openapi.UserResourceApi;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class UserResource implements UserResourceApi {
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity identity;

    @NoCache
    public UserResponse me() {
        if (identity.getPrincipal() instanceof io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) {
            final var jwtCallerPrincipal = (io.quarkus.oidc.runtime.OidcJwtCallerPrincipal) identity.getPrincipal();
            final var jwtClaims = jwtCallerPrincipal.getClaims();
            final String email = jwtClaims.getClaimValueAsString("email");
            return userService.findUserByEmail(email).orElseGet(() -> {
                String firstName = jwtClaims.getClaimValueAsString("given_name");
                String lastName = jwtClaims.getClaimValueAsString("family_name");
                UserRequest userRequest = new UserRequest(firstName, lastName, email, Collections.singletonList(Profile.User));
                final Long newId = userService.createUser(userRequest);
                return userService.findUserById(newId).orElseThrow(() -> {
                    logger.warn("Cannot persist a new userRequest into Repository. UserRequest=" + userRequest);
                    throw new NotAuthorizedException("invalid_client");
                });
            });
        } else {
            logger.debug("Unknown identity.getPrincipal: " + identity.getPrincipal());
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
