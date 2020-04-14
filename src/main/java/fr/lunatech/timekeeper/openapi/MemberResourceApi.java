package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/members")
public interface MemberResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newMember(MemberRequest request);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    MemberResponse readActivityById(@PathParam("id") long id);

}
