package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.dtos.UserUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserResponse> findUserById(Long id);
    Optional<UserResponse> findUserByEmail(String email);
    List<UserResponse> listAllUsers();
    Long createUser(UserCreateRequest user);
    Optional<Long> updateUser(Long id, UserUpdateRequest user);
    Optional<Long> deleteUser(Long id);
}
