package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.openapi.CustomerResourceApi;
import fr.lunatech.timekeeper.services.CustomerService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

public class CustomerResource implements CustomerResourceApi {

    @Inject
    CustomerService customerService;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Override
    public Response createCustomer(@Valid CustomerRequest request, UriInfo uriInfo) {
        final long customerId = customerService.addCustomer(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(customerId)).build();
        return Response.created(uri).build();
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        return customerService.getCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateCustomer(Long id, @Valid CustomerRequest request) {
        customerService.updateCustomer(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }
}
