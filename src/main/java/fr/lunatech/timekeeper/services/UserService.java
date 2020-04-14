package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Long createUser(UserRequest request) ;
    List<UserResponse> findAllUsers();
    Optional<UserResponse> findUserById(Long id);
}
