package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.ActivityCreateRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.dtos.ActivityUpdateRequest;
import fr.lunatech.timekeeper.resources.apis.ActivityResourceApi;
import fr.lunatech.timekeeper.services.interfaces.ActivityService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @Override
    public List<ActivityResponse> getAllActivities() {
        return activityService.listAllActivities();
    }

    @Override
    public Response createActivity(@Valid ActivityCreateRequest request, UriInfo uriInfo) {
        final Long activityId = activityService.createActivity(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(activityId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public ActivityResponse getActivity(Long id) {
        return activityService.findActivityById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateActivity(Long id, @Valid ActivityUpdateRequest request) {
        activityService.updateActivity(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @Override
    public Response deleteActivity(Long id) {
        activityService.deleteActivity(id);
        return Response.noContent().build();
    }

    //TODO à trancher si le delete ne devrait pas disparaitre car trop dangeraux, sinon reservé aux admins et 2 points suivants requis
    //TODO si ok pour delete : effacer une activité devrait vérifier si des Entry reference celle ci
    //TODO si ok pour delete : supprimer une activité devrait effacer les membres liés (soit pas SQL cascade soit par un autre mécanisme)
}
