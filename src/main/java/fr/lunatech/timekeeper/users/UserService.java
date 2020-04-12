package fr.lunatech.timekeeper.users;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(Long id);
    List<User> listAllUsers();
    Long insertUser(UserMutable user);
    Optional<Long> updateUser(Long id, UserMutable user);
    Optional<Long> deleteUser(Long id);
}
