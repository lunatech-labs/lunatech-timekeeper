package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.resources.apis.CustomerResourceApi;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.CustomerCreateRequest;
import fr.lunatech.timekeeper.dtos.CustomerUpdateRequest;
import fr.lunatech.timekeeper.services.interfaces.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class CustomerResource implements CustomerResourceApi {

    @Inject
    CustomerService customerService;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerService.listAllCustomers();
    }

    @Override
    public Response createCustomer(CustomerCreateRequest request) {
        return Response.ok(customerService.createCustomer(request)).build();
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        return customerService.findCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateCustomer(Long id, CustomerUpdateRequest request) {
        return Response.ok(customerService.updateCustomer(id, request).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteCustomer(Long id) {
        return Response.ok(customerService.deleteCustomer(id).orElse(id)).build();
    }

    //TODO delete ne doit etre accessible que par les admins et vérifier qu'unes activités ne pointent vers lui
}
