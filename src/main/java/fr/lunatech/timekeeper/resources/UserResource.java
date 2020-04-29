package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.openapi.UserResourceApi;
import fr.lunatech.timekeeper.resources.security.AuthenticatedUserInfo;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static fr.lunatech.timekeeper.resources.security.SecurityIdentityUtils.retrieveAuthenticatedUserInfo;

public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @Inject
    SecurityIdentity identity;

    @RolesAllowed({"user", "admin"})
    @NoCache
    @Override
    public UserResponse me() {
        final AuthenticatedUserInfo authenticatedUserInfo = retrieveAuthenticatedUserInfo(identity);
        final UserRequest userRequest = authenticatedUserInfo.toUserRequest();
        final String organization = authenticatedUserInfo.getOrganization();

        return userService.authenticate(userRequest, organization);
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public List<UserResponse> getAllUsers() {
        return userService.findAllUsers();
    }

    @RolesAllowed({"admin"})
    @Override
    public Response createUser(@Valid UserRequest request, UriInfo uriInfo) {
        final AuthenticatedUserInfo authenticatedUserInformation = retrieveAuthenticatedUserInfo(identity);

        final Long userId = userService.createUser(request, authenticatedUserInformation.getOrganization());
        final URI uri = uriInfo.getAbsolutePathBuilder().path(userId.toString()).build();
        return Response.created(uri).build();
    }

    @RolesAllowed({"user", "admin"})
    @Override
    public UserResponse getUser(Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @RolesAllowed({"admin"})
    @Override
    public Response updateUser(Long id, @Valid UserRequest request) {
        userService.updateUser(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

}
