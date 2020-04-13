package fr.lunatech.timekeeper.resources.apis;

import fr.lunatech.timekeeper.dtos.ActivityCreateRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
import fr.lunatech.timekeeper.dtos.ActivityUpdateRequest;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/activities")
public interface ActivityResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ActivityResponse> getAllActivities();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createActivity(@RequestBody ActivityCreateRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ActivityResponse getActivity(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateActivity(@PathParam("id") Long id, @RequestBody ActivityUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response deleteActivity(@PathParam("id") Long id);

}
