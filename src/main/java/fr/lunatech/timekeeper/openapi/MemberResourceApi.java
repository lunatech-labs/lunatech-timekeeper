package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/api/activities/{activityId}/members")
public interface MemberResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addMemberToActivity(@PathParam("activityId") Long activityId, @RequestBody MemberRequest request, @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    MemberResponse getMember(@PathParam("activityId") Long activityId, @PathParam("id") Long id);

}
