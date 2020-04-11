package fr.lunatech.timekeeper.members;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities/{activityId}/members")
public interface MemberResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Member> getAllMembersOfActivity(@PathParam("activityId") Long activityId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addMemberToActivity(@PathParam("activityId") Long activityId, MemberMutable member);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    Member getMember(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response changeMemberRole(@PathParam("activityId") Long activityId, @PathParam("id") Long id, Role role);

    @DELETE
    @Path("/{id}")
    Response removeMemberToActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

}
