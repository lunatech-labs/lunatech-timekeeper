package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.openapi.ActivityResourceApi;
import fr.lunatech.timekeeper.services.ActivityService;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/api/activities")
public class ActivityResource implements ActivityResourceApi {

    @Inject
    ActivityService activityService;

    @Override
    public List<ActivityResponse> getAllActivities() {
        return activityService.listAllActivities();
    }

    @Override
    public Response createActivity(@Valid ActivityRequest request, UriInfo uriInfo) {
        final long activityId = activityService.createActivity(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(activityId)).build();
        return Response.created(uri).build();
    }

    @Override
    public ActivityResponse getActivity(Long id) {
        return activityService.findActivityById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateActivity(Long id, @Valid ActivityRequest request) {
        activityService.updateActivity(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
