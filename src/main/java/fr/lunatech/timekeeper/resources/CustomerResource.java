package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.openapi.CustomerResourceApi;
import fr.lunatech.timekeeper.services.CustomerService;
import fr.lunatech.timekeeper.services.dto.CustomerDto;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api/customers")
public class CustomerResource implements CustomerResourceApi {

    @Inject
    CustomerService customerService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CustomerDto> readAllCustomers() {
        return customerService.getAllCustomers();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(CustomerDto customerDto) {
        return Response.ok(customerService.addCustomer(customerDto)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerDto readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") long id, CustomerDto customerDto){
        return Response.ok(customerService.updateCustomer(id, customerDto).orElseThrow(NotFoundException::new)).build();
    }

}
