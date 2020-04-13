package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.dtos.UserUpdateRequest;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Optional<UserResponse> findUserById(Long id) {
        return User.<User>findByIdOptional(id).map(this::response);
    }

    @Override
    public Optional<UserResponse> findUserByEmail(String email) {
        return User.<User>find("email", email).firstResultOptional().map(this::response);
    }

    @Override
    public List<UserResponse> listAllUsers() {
        try (final Stream<User> users = User.streamAll()) {
            return users.map(this::response).collect(Collectors.toList());
        }
    }

    @Override
    public Long count() {
        return User.count();
    }

    @Transactional
    @Override
    public Long createUser(UserCreateRequest request) {
        final var user = new User();
        User.persist(bind(user, request));
        return user.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserUpdateRequest request) {
        return User.<User>findByIdOptional(id).map(user -> bind(user, request).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteUser(Long id) {
        return User.<User>findByIdOptional(id)
                .map(user -> {
                    final var oldId = user.id;
                    if (user.isPersistent()) {
                        user.delete();
                    }
                    return oldId;
                });
    }

    public UserResponse response(User user) {
        return new UserResponse(user.id, user.firstName, user.lastName, user.email, user.profiles);
    }

    public User bind(User user, UserCreateRequest request) {
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.profiles = request.getProfiles();
        return user;
    }

    public User bind(User user, UserUpdateRequest request) {
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.profiles = request.getProfiles();
        return user;
    }
}
