package fr.lunatech.timekeeper.users;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class UserResource implements UserResourceApi {

    @Inject
    UserService userService;

    @Override
    public List<User> getAllUsers() {
        return userService.listAllUsers();
    }

    @Override
    public Response createUser(UserMutable user) {
        return Response.ok(userService.insertUser(user)).build();
    }

    @Override
    public User getUser(Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateUser(Long id, UserMutable user) {
        return Response.ok(userService.updateUser(id, user).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteUser(Long id) {
        return Response.ok(userService.deleteUser(id).orElseThrow(NotFoundException::new)).build();
    }

    //TODO le remove ne doit être accessible que les admins ou ne pas exister
    //TODO le delete si il reste doit vérifier qu'il n'est pas présent dans un Member ou dans une Entry
}
