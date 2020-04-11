package fr.lunatech.timekeeper.activities;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities")
public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public List<Activity> getAllActivities() {
        return activityService.listAllActivities();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response createActivity(Activity activity) {
        return Response.ok(activityService.insertActivity(activity)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Activity getActivity(@PathParam("id") Long id) {
        return activityService.findActivityById(id).orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response updateActivity(@PathParam("id") Long id, Activity activity) {
        return Response.ok(activityService.updateActivity(id, activity).orElseThrow(NotFoundException::new)).build();
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response deleteActivity(@PathParam("id") Long id) {
        return Response.ok(activityService.deleteActivity(id).orElseThrow(NotFoundException::new)).build();
    }
}
