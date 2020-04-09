package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpRespHandler;
import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.MemberDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/members")
public class MemberResource extends HttpRespHandler {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newMember(MemberDto memberDto) {
        return statusCodeHandler(() -> userTkService.addMember(memberDto));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public MemberDto readActivityById(@PathParam("id") long id) {
        return notFoundHandler(userTkService.getMemberById(id));
    }

}
