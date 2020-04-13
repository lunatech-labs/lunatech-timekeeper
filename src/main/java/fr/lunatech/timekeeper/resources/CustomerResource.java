package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.dtos.CustomerCreateRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.CustomerUpdateRequest;
import fr.lunatech.timekeeper.resources.apis.CustomerResourceApi;
import fr.lunatech.timekeeper.services.interfaces.CustomerService;

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
        return customerService.listAllCustomers();
    }

    @Override
    public Response createCustomer(@Valid CustomerCreateRequest request, UriInfo uriInfo) {
        final Long customerId = customerService.createCustomer(request);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(customerId.toString()).build();
        return Response.created(uri).build();
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        return customerService.findCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateCustomer(Long id, @Valid CustomerUpdateRequest request) {
        customerService.updateCustomer(id, request).orElseThrow(NotFoundException::new);
        return Response.noContent().build();
    }

    @Override
    public Response deleteCustomer(Long id) {
        customerService.deleteCustomer(id);
        return Response.noContent().build();
    }

    //TODO delete ne doit etre accessible que par les admins et vérifier qu'unes activités ne pointent vers lui
}
