package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.entities.UserEntity;
import fr.lunatech.timekeeper.models.User;
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
    public Optional<User> findUserById(Long id) {
        return UserEntity.<UserEntity>findByIdOptional(id).map(User::new);
    }

    @Override
    public List<User> listAllUsers() {
        try (final Stream<UserEntity> entities = UserEntity.streamAll()) {
            return entities.map(User::new).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertUser(User user) {
        final var entity = new UserEntity();
        UserEntity.persist(bind(entity, user));
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateUser(Long id, User user) {
        return UserEntity.<UserEntity>findByIdOptional(id).map(entity -> bind(entity, user).id);
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

    private static UserEntity bind(UserEntity entity, User user) {
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.email = user.getEmail();
        entity.profiles = user.getProfiles();
        return entity;
    }
}
