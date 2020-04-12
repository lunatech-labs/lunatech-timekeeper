package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserById(Long id);
    List<User> listAllUsers();
    Long insertUser(User user);
    Optional<Long> updateUser(Long id, User user);
    Optional<Long> deleteUser(Long id);
}
