package fr.lunatech.timekeeper.resources.apis;

import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.CustomerCreateRequest;
import fr.lunatech.timekeeper.dtos.CustomerUpdateRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/customers")
public interface CustomerResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CustomerResponse> getAllCustomers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createCustomer(CustomerCreateRequest request);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomerResponse getCustomer(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateCustomer(@PathParam("id") Long id, CustomerUpdateRequest request);

    @DELETE
    @Path("/{id}")
    Response deleteCustomer(@PathParam("id") Long id);

}
