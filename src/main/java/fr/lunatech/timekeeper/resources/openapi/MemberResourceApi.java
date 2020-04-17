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

@Path("/api/projects/{projectId}/members")
public interface MemberResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<MemberResponse> getAllMembersOfProject(@PathParam("projectId") Long projectId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addMemberToProject(@PathParam("projectId") Long projectId, @RequestBody MemberRequest request, @Context UriInfo uriInfo);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    MemberResponse getMember(@PathParam("projectId") Long projectId, @PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateMember(@PathParam("projectId") Long projectId, @PathParam("id") Long id, @RequestBody MemberUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response removeMemberToProject(@PathParam("projectId") Long projectId, @PathParam("id") Long id);

}
