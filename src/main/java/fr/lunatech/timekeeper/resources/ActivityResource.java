package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.services.dto.ActivityDto;
import fr.lunatech.timekeeper.services.ActivityService;


import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/activities")
public class ActivityResource {

    @Inject
    ActivityService activityService;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(ActivityDto activity) {
        return activityService.addActivity(activity);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<ActivityDto> readActivityById(@PathParam("id") long id) {
        return activityService.getActivityById(id);
    }

}
