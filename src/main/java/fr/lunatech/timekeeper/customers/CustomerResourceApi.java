package fr.lunatech.timekeeper.customers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/customers")
public interface CustomerResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Customer> getAllCustomers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createCustomer(CustomerMutable customer);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Customer getCustomer(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateCustomer(@PathParam("id") Long id, CustomerMutable customer);

    @DELETE
    @Path("/{id}")
    Response deleteCustomer(@PathParam("id") Long id);

}
