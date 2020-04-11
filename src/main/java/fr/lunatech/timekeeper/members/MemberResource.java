package fr.lunatech.timekeeper.members;

import javax.inject.Inject;
import javax.persistence.Convert;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MemberResource implements MemberResourceApi {

    @Inject
    MemberService memberService;

    @Override
    public List<Member> getAllMembersOfActivity(Long activityId) {
        return memberService.listAllMembers(activityId);
    }

    @Override
    public Response addMemberToActivity(Long activityId, MemberMutable member) {
        return Response.ok(memberService.insertMember(activityId, member)).build();
    }

    @Override
    public Member getMember(Long activityId, Long id) {
        return memberService.findMemberById(activityId, id).orElseThrow(NotFoundException::new);
    }

    @Convert(converter = Role.Converter.class)
    @Override
    public Response changeMemberRole(Long activityId, Long id, Role role) {
        return Response.ok(memberService.changeRole(activityId, id, role).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response removeMemberToActivity(Long activityId, Long id) {
        return Response.ok(memberService.deleteMember(activityId, id).orElseThrow(NotFoundException::new)).build();
    }
}
