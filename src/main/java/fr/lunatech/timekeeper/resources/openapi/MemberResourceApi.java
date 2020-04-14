package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.dtos.MemberUpdateRequest;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/activities/{activityId}/members")
public interface MemberResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MemberResponse> getAllMembersOfActivity(@PathParam("activityId") Long activityId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addMemberToActivity(@PathParam("activityId") Long activityId, @RequestBody MemberRequest request, @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    MemberResponse getMember(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateMember(@PathParam("activityId") Long activityId, @PathParam("id") Long id, @RequestBody MemberUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response removeMemberToActivity(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

}
