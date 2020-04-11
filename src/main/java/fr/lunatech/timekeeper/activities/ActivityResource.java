package fr.lunatech.timekeeper.activities;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
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
    public Response createActivity(ActivityMutable activity) {
        return Response.ok(activityService.insertActivity(activity)).build();
    }

    @Override
    public Activity getActivity(Long id) {
        return activityService.findActivityById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateActivity(Long id, ActivityMutable activity) {
        return Response.ok(activityService.updateActivity(id, activity).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteActivity(Long id) {
        return Response.ok(activityService.deleteActivity(id).orElseThrow(NotFoundException::new)).build();
    }
}
