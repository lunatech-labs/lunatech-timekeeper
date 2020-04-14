package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.UserRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;

import java.util.Optional;

public interface UserService {

    long addUser(UserRequest request);

    Optional<UserResponse> getUserById(long id);

    long addMember(MemberRequest request);

    Optional<MemberResponse> getMemberById(long id);
}
