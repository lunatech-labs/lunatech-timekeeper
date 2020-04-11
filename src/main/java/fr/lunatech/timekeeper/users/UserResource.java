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
    public Response createUser(User user) {
        return Response.ok(userService.insertUser(user)).build();
    }

    @Override
    public User getUser(Long id) {
        return userService.findUserById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateUser(Long id, User user) {
        return Response.ok(userService.updateUser(id, user).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteUser(Long id) {
        return Response.ok(userService.deleteUser(id).orElseThrow(NotFoundException::new)).build();
    }

}
