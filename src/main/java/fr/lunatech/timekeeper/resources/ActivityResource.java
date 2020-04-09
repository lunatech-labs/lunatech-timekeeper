package fr.lunatech.timekeeper.resources;


import fr.lunatech.timekeeper.resources.utils.HttpRespHandler;
import fr.lunatech.timekeeper.services.ActivityService;
import fr.lunatech.timekeeper.services.dto.ActivityDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/activities")
public class ActivityResource extends HttpRespHandler {

    @Inject
    ActivityService activityService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(ActivityDto activity) {
        return statusCodeHandler(() -> activityService.addActivity(activity));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public ActivityDto readActivityById(@PathParam("id") long id) {
        return notFoundHandler(activityService.getActivityById(id));
    }




}
