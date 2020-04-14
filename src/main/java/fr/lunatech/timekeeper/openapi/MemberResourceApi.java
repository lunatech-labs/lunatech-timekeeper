package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.services.dto.MemberDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/members")
public interface MemberResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newMember(MemberDto memberDto);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    MemberDto readActivityById(@PathParam("id") long id);

}
