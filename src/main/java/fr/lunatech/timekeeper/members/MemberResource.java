package fr.lunatech.timekeeper.members;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class MemberResource implements MemberResourceApi {

    @Inject
    MemberService memberService;

    @Override
    public List<Member> getAllMembers(Long activityId) {
        return memberService.listAllMembers(activityId);
    }

    @Override
    public Response createMember(Long activityId, Member member) {
        return Response.ok(memberService.insertMember(activityId, member)).build();
    }

    @Override
    public Member getActivity(Long activityId, Long id) {
        return memberService.findMemberById(activityId, id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateActivity(Long activityId, Long id, Member member) {
        return Response.ok(memberService.updateMember(activityId, id, member).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteActivity(Long activityId, Long id) {
        return Response.ok(memberService.deleteMember(activityId, id).orElseThrow(NotFoundException::new)).build();
    }
}
