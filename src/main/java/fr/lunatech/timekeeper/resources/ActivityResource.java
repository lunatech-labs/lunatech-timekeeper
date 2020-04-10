package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.ActivityService;
import fr.lunatech.timekeeper.services.dto.ActivityDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/activities")
public class ActivityResource {

    @Inject
    ActivityService activityService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(ActivityDto activity) {
        return Response.ok(activityService.addActivity(activity)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public ActivityDto readActivityById(@PathParam("id") long id) {
        return activityService.getActivityById(id).orElseThrow(NotFoundException::new);
    }


}
