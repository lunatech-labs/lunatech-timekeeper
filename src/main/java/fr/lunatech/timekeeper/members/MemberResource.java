package fr.lunatech.timekeeper.members;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities/{activityId}/members")
public class MemberResource implements MemberResourceApi {

    @Inject
    MemberService memberService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Member> getAllMembers(@PathParam("activityId") Long activityId) {
        return memberService.listAllMembers(activityId);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMember(@PathParam("activityId") Long activityId, Member member) {
        return Response.ok(memberService.insertMember(activityId, member)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Member getActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id) {
        return memberService.findMemberById(activityId, id).orElseThrow(NotFoundException::new);
    }

}
