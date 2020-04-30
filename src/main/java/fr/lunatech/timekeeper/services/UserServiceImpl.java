package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Organization;
import fr.lunatech.timekeeper.models.User;
import fr.lunatech.timekeeper.services.dtos.RoleInProjectResponse;
import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;
import fr.lunatech.timekeeper.services.exceptions.IllegalEntityStateException;
import fr.lunatech.timekeeper.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.hibernate.internal.util.StringHelper.isNotEmpty;

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
    public Long createUser(UserRequest request, String organization) {
        logger.debug("Create a new user with organization={}, request={}", organization, request);
        final var user = unbind(request, organization);
        User.persist(user);

        return user.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserRequest request) {
        logger.debug("Modify user for userId={} with request={}", id, request);
        return User.<User>findByIdOptional(id).map(user -> unbind(user, request).id);
    }

    @Transactional
    @Override
    public UserResponse authenticate(UserRequest request, String organization) {
        final User authenticatedUser = User.<User>find("email", request.getEmail())
                .firstResultOptional()
                .map(user -> {
                    logger.debug("Modify user by email={} (if a change is detected) with organization={}, request={}", user.email, organization, request);
                    /* Panache optimizes this process, no need to check if a change is necessary */
                    return unbind(user, request, organization);
                })
                .orElseGet(() -> {
                    logger.debug("Create a new user by email={} with organization={}, request={}", request.getEmail(), organization, request);
                    final var user = unbind(request, organization);
                    User.persist(user);
                    return user;
                });

        return bind(authenticatedUser);
    }

    @Override
    public Long count() {
        return User.count();
    }

    private UserResponse bind(User user) {
        final List<RoleInProjectResponse> rolesInProjects = (user.rolesInProjects == null)
                ? emptyList()
                : user.rolesInProjects.stream()
                .map(roleInProject -> new RoleInProjectResponse(roleInProject.id, roleInProject.user.id, roleInProject.role, roleInProject.project.id))
                .collect(Collectors.toList());

        return new UserResponse(user.id, user.firstName, user.lastName, user.email, user.picture, user.profiles, rolesInProjects, user.organization.id);
    }

    private User unbind(User user, UserRequest request, String organization) {
        user.organization = isNotEmpty(organization) ? findOrganization(organization) : user.organization;
        user.firstName = request.getFirstName();
        user.lastName = request.getLastName();
        user.email = request.getEmail();
        user.picture = request.getPicture();
        user.profiles = request.getProfiles();
        return user;
    }

    private User unbind(User user, UserRequest request) {
        return unbind(user, request, null);
    }

    private User unbind(UserRequest request, String organization) {
        return unbind(new User(), request, organization);
    }

    private Organization findOrganization(String organization) {
        return Organization.find("tokenName", organization)
                .<Organization>firstResultOptional()
                .orElseThrow(() -> new IllegalEntityStateException(String.format("Unknown organization. organization=%s", organization)));
    }
}
