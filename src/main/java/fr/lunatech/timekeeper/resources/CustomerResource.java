package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.openapi.CustomerResourceApi;
import fr.lunatech.timekeeper.services.CustomerService;

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
    public List<CustomerResponse> readAllCustomers() {
        return customerService.getAllCustomers();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newCustomer(CustomerRequest request) {
        return Response.ok(customerService.addCustomer(request)).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CustomerResponse readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("id") long id, CustomerRequest request){
        return Response.ok(customerService.updateCustomer(id, request).orElseThrow(NotFoundException::new)).build();
    }

}
