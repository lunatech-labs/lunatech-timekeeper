package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.MemberRequest;
import fr.lunatech.timekeeper.dtos.MemberResponse;
import fr.lunatech.timekeeper.openapi.MemberResourceApi;
import fr.lunatech.timekeeper.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/members")
public class MemberResource implements MemberResourceApi {

    @Inject
    UserService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newMember(MemberRequest request) {
        return Response.ok(userTkService.addMember(request)).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public MemberResponse readActivityById(@PathParam("id") long id) {
        return userTkService.getMemberById(id).orElseThrow(NotFoundException::new);
    }

}
