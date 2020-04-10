package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.services.dto.ActivityDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/activities")
public interface ActivityResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newCustomer(ActivityDto activity);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ActivityDto readActivityById(@PathParam("id") long id);

}
