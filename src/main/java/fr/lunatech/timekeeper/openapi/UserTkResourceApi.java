package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.services.dto.UserTkDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tkusers")
public interface UserTkResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newCustomer(UserTkDto userTkDto);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    UserTkDto readActivityById(@PathParam("id") long id);

}
