package fr.lunatech.timekeeper.resources.apis;

import fr.lunatech.timekeeper.dtos.UserCreateRequest;
import fr.lunatech.timekeeper.dtos.UserResponse;
import fr.lunatech.timekeeper.dtos.UserUpdateRequest;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/users")
public interface UserResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "countGetAllUsers", description = "Counts how many times the getAllUsers method has been invoked")
    @Timed(name = "timeGetAllUsers", description = "Times how long it takes to invoke the getAllUsers method", unit = MetricUnits.MILLISECONDS)
    List<UserResponse> getAllUsers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createUser(@RequestBody UserCreateRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    UserResponse getUser(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateUser(@PathParam("id") Long id, @RequestBody UserUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response deleteUser(@PathParam("id") Long id);
}
