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
        return Response.ok(activityService.deleteActivity(id).orElse(id)).build();
    }

    //TODO à trancher si le delete ne devrait pas disparaitre car trop dangeraux, sinon reservé aux admins et 2 points suivants requis
    //TODO si ok pour delete : effacer une activité devrait vérifier si des Entry reference celle ci
    //TODO si ok pour delete : supprimer une activité devrait effacer les membres liés (soit pas SQL cascade soit par un autre mécanisme)
}
