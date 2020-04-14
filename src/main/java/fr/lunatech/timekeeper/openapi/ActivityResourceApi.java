package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/activities")
public interface ActivityResourceApi {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newCustomer(ActivityRequest request);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ActivityResponse readActivityById(@PathParam("id") long id);

}
