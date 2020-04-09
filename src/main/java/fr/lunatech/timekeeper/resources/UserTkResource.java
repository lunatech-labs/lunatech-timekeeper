package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.utils.HttpRespHandler;
import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.UserTkDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users")
public class UserTkResource extends HttpRespHandler {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(UserTkDto userTkDto) {
        return statusCodeHandler(() -> userTkService.addUserTk(userTkDto));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public UserTkDto readActivityById(@PathParam("id") long id) {
        return notFoundHandler(userTkService.getUserTkById(id));
    }

}
