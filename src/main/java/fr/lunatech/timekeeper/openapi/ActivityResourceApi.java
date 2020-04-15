package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.dtos.ActivityRequest;
import fr.lunatech.timekeeper.dtos.ActivityResponse;
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
    Response createActivity(@RequestBody ActivityRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ActivityResponse getActivity(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateActivity(@PathParam("id") Long id, @RequestBody ActivityRequest request);

}
