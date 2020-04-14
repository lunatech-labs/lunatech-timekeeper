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

public class CustomerResource implements CustomerResourceApi {

    @Inject
    CustomerService customerService;

    public List<CustomerResponse> readAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Response newCustomer(CustomerRequest request) {
        return Response.ok(customerService.addCustomer(request)).build();
    }

    public CustomerResponse readCustomerById(@PathParam("id") long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
    }

    public Response updateCustomer(@PathParam("id") long id, CustomerRequest request){
        return Response.ok(customerService.updateCustomer(id, request).orElseThrow(NotFoundException::new)).build();
    }

}
