package fr.lunatech.timekeeper.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Optional<User> findUserById(Long id) {
        return UserEntity.<UserEntity>findByIdOptional(id).map(this::fromEntity);
    }

    @Override
    public List<User> listAllUsers() {
        try (final Stream<UserEntity> entities = UserEntity.streamAll()) {
            return entities.map(this::fromEntity).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertUser(UserMutable user) {
        final var entity = toEntity(user);
        UserEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, UserMutable user) {
        return UserEntity.<UserEntity>findByIdOptional(id).map(entity -> fillEntity(entity, user).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteUser(Long id) {
        return UserEntity.<UserEntity>findByIdOptional(id)
                .map(entity -> {
                    final var oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private UserEntity toEntity(UserMutable user) {
        final var entity = new UserEntity();
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.email = user.getEmail();
        entity.profiles = user.getProfiles();
        return entity;
    }

    private UserEntity fillEntity(UserEntity entity, UserMutable user) {
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.email = user.getEmail();
        entity.profiles = isNotEmpty(user.getProfiles()) ? user.getProfiles() : emptyList();
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
