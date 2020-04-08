package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/customers")
public class CustomerResource {

    @Inject
    CustomerService customerService;


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(Customer customer) {
        return customerService.addCustomer(customer);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<Customer> readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id);
    }


}