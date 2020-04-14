package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.openapi.ActivityResourceApi;
import fr.lunatech.timekeeper.services.ActivityService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("/api/activities")
public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @Override
    public Response createActivity(@Valid ActivityRequest request, UriInfo uriInfo) {
        final long activityId = activityService.addActivity(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(activityId)).build();
        return Response.created(uri).build();
    }

    @Override
    public ActivityResponse getActivity(Long id) {
        return activityService.getActivityById(id).orElseThrow(NotFoundException::new);
    }
}
