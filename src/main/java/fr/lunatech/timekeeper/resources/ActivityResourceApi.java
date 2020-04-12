package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Activity;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities")
public interface ActivityResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Activity> getAllActivities();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createActivity(Activity activity);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Activity getActivity(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateActivity(@PathParam("id") Long id, Activity activity);

    @DELETE
    @Path("/{id}")
    Response deleteActivity(@PathParam("id") Long id);

}
