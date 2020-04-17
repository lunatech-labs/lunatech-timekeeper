package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.services.dtos.ClientRequest;
import fr.lunatech.timekeeper.services.dtos.ClientResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/clients")
public interface ClientResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<ClientResponse> getAllClients();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createClient(@RequestBody ClientRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ClientResponse getClient(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateClient(@PathParam("id") Long id, @RequestBody ClientRequest request);

}
