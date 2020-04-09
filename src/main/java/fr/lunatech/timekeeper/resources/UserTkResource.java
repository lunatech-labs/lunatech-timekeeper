package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.UserTkService;
import fr.lunatech.timekeeper.services.dto.UserTkDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/users")
public class UserTkResource {

    @Inject
    UserTkService userTkService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(UserTkDto userTkDto) {
        return userTkService.addUserTk(userTkDto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<UserTkDto> readActivityById(@PathParam("id") long id) {
        return userTkService.getUserTkById(id);
    }

}
