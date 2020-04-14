package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.openapi.MemberResourceApi;
import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.MemberDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/members")
public class MemberResource implements MemberResourceApi {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newMember(MemberDto memberDto) {
        return Response.ok(userTkService.addMember(memberDto)).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MemberDto readActivityById(@PathParam("id") long id) {
        return userTkService.getMemberById(id).orElseThrow(NotFoundException::new);
    }

}
