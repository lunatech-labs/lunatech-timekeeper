package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import fr.lunatech.timekeeper.services.interfaces.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Override
    public Optional<UserResponse> findUserById(Long id) {
        return User.<User>findByIdOptional(id).map(this::from);
    }

    @Override
    public Optional<UserResponse> findUserByEmail(String email) {
        return User.<User>find("email", email).firstResultOptional().map(this::from);
    }

    @Override
    public List<UserResponse> findAllUsers() {
        try (final Stream<User> users = User.streamAll()) {
            return users.map(this::from).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createUser(UserRequest request) {
        final var user = bind(request);
        User.persist(user);
        return user.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserRequest request) {
        return User.<User>findByIdOptional(id).map(user -> bind(user, request).id);
    }

    @Override
    public Long count() {
        return User.count();
    }

    private UserResponse from(User user) {
        return new UserResponse(user.id, user.firstName, user.lastName, user.email, user.profiles, user.members.stream().map(m -> m.id).collect(Collectors.toList()));
    }

    private User bind(User user, UserRequest request) {
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.profiles = request.getProfiles();
        return user;
    }

    private User bind(UserRequest request) {
        return bind(new User(), request);
    }
}
