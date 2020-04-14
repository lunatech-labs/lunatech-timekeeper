package fr.lunatech.timekeeper.openapi;

import fr.lunatech.timekeeper.services.dto.CustomerDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/customers")
public interface CustomerResourceApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<CustomerDto> readAllCustomers();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response newCustomer(CustomerDto customerDto);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    CustomerDto readCustomerById(@PathParam("id") long id);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response updateCustomer(@PathParam("id") long id, CustomerDto customerDto);

}
