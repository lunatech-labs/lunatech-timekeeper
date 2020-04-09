package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.resources.utils.HttpRespHandler;
import fr.lunatech.timekeeper.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/customers")
public class CustomerResource extends HttpRespHandler {

    @Inject
    CustomerService customerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(Customer customer) {
        return statusCodeHandler(() -> customerService.addCustomer(customer));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Customer readCustomerById(@PathParam("id") long id) {
        return notFoundHandler(customerService.getCustomerById(id));
    }


}
