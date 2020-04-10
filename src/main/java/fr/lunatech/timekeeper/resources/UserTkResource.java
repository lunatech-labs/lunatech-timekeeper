package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.UserTkDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tkusers")
public class UserTkResource {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(UserTkDto userTkDto) {
        return Response.ok(userTkService.addUserTk(userTkDto)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public UserTkDto readActivityById(@PathParam("id") long id) {
        return userTkService.getUserTkById(id).orElseThrow(NotFoundException::new);
    }

}
