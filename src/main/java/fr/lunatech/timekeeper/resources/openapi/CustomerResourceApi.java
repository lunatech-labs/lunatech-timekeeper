package fr.lunatech.timekeeper.resources.openapi;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("/api/customers")
public interface CustomerResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CustomerResponse> getAllCustomers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createCustomer(@RequestBody CustomerRequest request, @Context UriInfo uriInfo);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomerResponse getCustomer(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateCustomer(@PathParam("id") Long id, @RequestBody CustomerRequest request);

}
