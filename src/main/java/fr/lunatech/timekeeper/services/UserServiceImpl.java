package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.models.User;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public Optional<UserResponse> findUserById(Long id) {
        return User.<User>findByIdOptional(id).map(this::from);
    }

    @Transactional
    @Override
    public Long createUser(UserRequest request) {
        final var user = new User();
        User.persist(bind(user, request));
        return user.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserRequest request) {
        return User.<User>findByIdOptional(id).map(user -> bind(user, request).id);
    }

    private UserResponse from(User user) {
        return new UserResponse(user.id, user.firstName, user.lastName, user.email, user.profiles);
    }

    private User bind(User user, UserRequest request) {
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.profiles = request.getProfiles();
        return user;
    }
}
