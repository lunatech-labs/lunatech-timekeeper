package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.services.CustomerService;
import fr.lunatech.timekeeper.services.dto.CustomerDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/customers")
public class CustomerResource {

    @Inject
    CustomerService customerService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(CustomerDto customerDto) {
        return Response.ok(customerService.addCustomer(customerDto)).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerDto> readAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")

    public CustomerDto readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Optional<Long> updateCustomer(@PathParam("id") long id, CustomerDto customerDto){
        return customerService.updateCustomer(id, customerDto);
    }

}
