package fr.lunatech.timekeeper.members;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities/{activityId}/members")
public interface MemberResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Member> getAllMembers(@PathParam("activityId") Long activityId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createMember(@PathParam("activityId") Long activityId, Member member);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    Member getActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id, Member member);

    @DELETE
    @Path("/{id}")
    Response deleteActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

}
