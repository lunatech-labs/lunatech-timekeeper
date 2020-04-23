package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.MemberRequest;
import fr.lunatech.timekeeper.services.dtos.MemberResponse;
import fr.lunatech.timekeeper.services.dtos.MemberUpdateRequest;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api")
public interface MemberResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/members")
    List<MemberResponse> getAllMembers();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/members")
    List<MemberResponse> getAllMembersOfProject(@PathParam("projectId") Long projectId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/members")
    Response addMemberToProject(@PathParam("projectId") Long projectId, @RequestBody MemberRequest request, @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/members/{id}")
    MemberResponse getMember(@PathParam("projectId") Long projectId, @PathParam("id") Long id);

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/members/{id}")
    Response updateMember(@PathParam("projectId") Long projectId, @PathParam("id") Long id, @RequestBody MemberUpdateRequest request);

    @DELETE
    @Path("/projects/{projectId}/members/{id}")
    Response removeMemberToProject(@PathParam("projectId") Long projectId, @PathParam("id") Long id);

}
