package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.MemberDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/members")
public class MemberResource {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newMember(MemberDto memberDto) {
        return userTkService.addMember(memberDto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<MemberDto> readActivityById(@PathParam("id") long id) {
        return userTkService.getMemberById(id);
    }

}
