package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.services.CustomerService;
import fr.lunatech.timekeeper.services.dto.CustomerDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/customers")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public long newCustomer(CustomerDto customerDto) {
        return customerService.addCustomer(customerDto);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerDto> readAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<CustomerDto> readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<Long> updateCustomer(@PathParam("id") long id, CustomerDto customerDto){
        return customerService.updateCustomer(id, customerDto);
    }

}
