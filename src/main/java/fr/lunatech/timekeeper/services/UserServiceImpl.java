package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
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
        return User.<User>findByIdOptional(id).map(this::bind);
    }

    @Override
    public Optional<UserResponse> findUserByEmail(String email) {
        return User.<User>find("email", email).firstResultOptional().map(this::bind);
    }

    @Override
    public List<UserResponse> findAllUsers() {
        try (final Stream<User> users = User.streamAll()) {
            return users.map(this::bind).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createUser(UserRequest request) {
        logger.debug("Create a new user with request={}", request);
        final var user = unbind(request);
        User.persist(user);
        return user.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserRequest request) {
        logger.debug("Modify user for userId={} with request={}", id, request);
        return User.<User>findByIdOptional(id).map(user -> unbind(user, request).id);
    }

    @Override
    public Long count() {
        return User.count();
    }

    private UserResponse bind(User user) {
        final List<MemberResponse> members = user.members
                .stream()
                .map(member -> new MemberResponse(member.id, member.user.id, member.role, member.project.id))
                .collect(Collectors.toList());

        return new UserResponse(user.id, user.firstName, user.lastName, user.email, user.profiles, members);
    }

    private User unbind(User user, UserRequest request) {
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.profiles = request.getProfiles();
        return user;
    }

    private User unbind(UserRequest request) {
        return unbind(new User(), request);
    }
}
