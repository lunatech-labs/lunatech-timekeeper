package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.MemberUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<MemberResponse> findMemberById(Long activityId, Long id);
    List<MemberResponse> listAllMembers(Long activityId);
    Long createMember(Long activityId, MemberRequest request);
    Optional<Long> updateMember(Long activityId, Long id, MemberUpdateRequest request);
    Optional<Long> deleteMember(Long activityId, Long id);
}
