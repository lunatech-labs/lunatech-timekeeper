package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.MemberUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<MemberResponse> findMemberById(Long projectId, Long id);
    List<MemberResponse> listAllMembers(Long projectId);
    Long createMember(Long projectId, MemberRequest request);
    Optional<Long> updateMember(Long projectId, Long id, MemberUpdateRequest request);
    Optional<Long> deleteMember(Long projectId, Long id);
}
