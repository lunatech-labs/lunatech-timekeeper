package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.ProjectRequest;
import fr.lunatech.timekeeper.services.dtos.ProjectResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/projects")
public interface ProjectResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ProjectResponse> getAllProjects();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createproject(@RequestBody ProjectRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ProjectResponse getproject(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateproject(@PathParam("id") Long id, @RequestBody ProjectRequest request);

}
