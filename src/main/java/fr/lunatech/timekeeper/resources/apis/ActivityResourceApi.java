package fr.lunatech.timekeeper.resources.apis;

import fr.lunatech.timekeeper.dtos.ActivityCreateRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.dtos.ActivityUpdateRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/activities")
public interface ActivityResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ActivityResponse> getAllActivities();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createActivity(ActivityCreateRequest request);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ActivityResponse getActivity(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateActivity(@PathParam("id") Long id, ActivityUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response deleteActivity(@PathParam("id") Long id);

}
