package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.dto.MemberDto;
import fr.lunatech.timekeeper.services.dto.UserTkDto;

import java.util.Optional;

public interface UserTkService {

    long addUserTk(UserTkDto userTkDto);

    Optional<UserTkDto> getUserTkById(long id);

    long addMember(MemberDto memberDto);

    Optional<MemberDto> getMemberById(long id);
}
