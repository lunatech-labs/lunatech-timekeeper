package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.dtos.MemberCreateRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<MemberResponse> findMemberById(Long activityId, Long id);

    List<MemberResponse> listAllMembers(Long activityId);

    Long createMember(Long activityId, MemberCreateRequest request);

    Optional<Long> updateMember(Long activityId, Long id, MemberUpdateRequest request);

    Optional<Long> deleteMember(Long activityId, Long id);
}
