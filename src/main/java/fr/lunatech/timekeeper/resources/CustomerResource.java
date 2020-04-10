package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/customers")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(Customer customer) {
        return Response.ok(customerService.addCustomer(customer)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Customer readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
    }


}
