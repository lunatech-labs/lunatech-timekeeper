package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;

import java.util.Optional;

public interface UserService {
    Long createUser(UserRequest request) ;
    Optional<UserResponse> findUserById(Long id);
    Optional<Long> updateUser(Long id, UserRequest user);

}
