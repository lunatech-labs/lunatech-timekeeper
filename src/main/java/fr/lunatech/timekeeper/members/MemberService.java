package fr.lunatech.timekeeper.members;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    Optional<Member> findMemberById(Long activityId, Long id);
    List<Member> listAllMembers(Long activityId);
    Long insertMember(Long activityId, MemberMutable member);
    Optional<Long> changeRole(Long activityId, Long id, Role role);
    Optional<Long> deleteMember(Long activityId, Long id);
}
