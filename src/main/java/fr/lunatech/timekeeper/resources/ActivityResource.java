package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.openapi.ActivityResourceApi;
import fr.lunatech.timekeeper.services.ActivityService;
import fr.lunatech.timekeeper.services.dto.ActivityDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/activities")
public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(ActivityDto activity) {
        return Response.ok(activityService.addActivity(activity)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ActivityDto readActivityById(@PathParam("id") long id) {
        return activityService.getActivityById(id).orElseThrow(NotFoundException::new);
    }

}
