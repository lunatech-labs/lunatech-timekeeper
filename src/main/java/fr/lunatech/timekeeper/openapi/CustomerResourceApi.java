package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/customers")
public interface CustomerResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CustomerResponse> readAllCustomers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newCustomer(CustomerRequest request);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomerResponse readCustomerById(@PathParam("id") long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateCustomer(@PathParam("id") long id, CustomerRequest request);

}
