package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.dtos.UserUpdateRequest;
import fr.lunatech.timekeeper.resources.apis.UserResourceApi;
import fr.lunatech.timekeeper.services.interfaces.UserService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userService.listAllUsers();
    }

    @Override
    public Response createUser(@Valid UserCreateRequest request, UriInfo uriInfo) {
        final Long userId = userService.createUser(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(userId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public UserResponse getUser(Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateUser(Long id, @Valid UserUpdateRequest request) {
        userService.updateUser(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @Override
    public Response deleteUser(Long id) {
        userService.deleteUser(id);
        return Response.noContent().build();
    }

    //TODO le remove ne doit être accessible que les admins ou ne pas exister
    //TODO le delete si il reste doit vérifier qu'il n'est pas présent dans un Member ou dans une Entry
}
