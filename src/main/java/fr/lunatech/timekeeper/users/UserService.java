package fr.lunatech.timekeeper.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    Optional<User> findUserById(Long id) {
        final Optional<UserEntity> entity = UserEntity.findByIdOptional(id);
        return entity.map(this::fromEntity);
    }

    List<User> listAllUsers() {
        try (final Stream<UserEntity> entities = UserEntity.streamAll()) {
            return entities.map(this::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    Long insertUser(User user) {
        final UserEntity entity = toEntity(user);
        UserEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    Optional<Long> updateUser(Long id, User user) {
        return UserEntity.<UserEntity>findByIdOptional(id)
                .map(entity -> fillEntity(entity, user).id);
    }

    @Transactional
    Optional<Long> deleteUser(Long id) {
        return UserEntity.<UserEntity>findByIdOptional(id)
                .map(entity -> {
                    final Long oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private UserEntity toEntity(User user) {
        final UserEntity entity = new UserEntity();
        entity.id = user.getId().orElse(null);
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.email = user.getEmail();
        entity.profiles = user.getProfiles();
        return entity;
    }

    private UserEntity fillEntity(UserEntity entity, User user) {
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.email = user.getEmail();
        entity.profiles = user.getProfiles();
        return entity;
    }

    private User fromEntity(UserEntity entity) {
        return new User(
                entity.id,
                entity.firstName,
                entity.lastName,
                entity.email,
                entity.profiles
        );
    }
}
