package fr.lunatech.timekeeper.activities;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @Override
    public List<Activity> getAllActivities() {
        return activityService.listAllActivities();
    }

    @Override
    public Response createActivity(Activity activity) {
        return Response.ok(activityService.insertActivity(activity)).build();
    }

    @Override
    public Activity getActivity(@PathParam("id") Long id) {
        return activityService.findActivityById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateActivity(@PathParam("id") Long id, Activity activity) {
        return Response.ok(activityService.updateActivity(id, activity).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteActivity(@PathParam("id") Long id) {
        return Response.ok(activityService.deleteActivity(id).orElseThrow(NotFoundException::new)).build();
    }
}
