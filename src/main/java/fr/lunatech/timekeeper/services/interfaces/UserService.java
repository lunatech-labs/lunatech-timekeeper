package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.UserRequest;
import fr.lunatech.timekeeper.services.dtos.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long createUser(UserRequest request);
    List<UserResponse> findAllUsers();
    Optional<UserResponse> findUserById(Long id);
    Optional<UserResponse> findUserByEmail(String email);
    Optional<Long> updateUser(Long id, UserRequest user);
    Long count();
}
