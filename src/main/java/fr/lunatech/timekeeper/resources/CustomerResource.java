package fr.lunatech.timekeeper.resources;

import fr.lunatech.timekeeper.models.Customer;
import fr.lunatech.timekeeper.services.CustomerService;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;

public class CustomerResource implements CustomerResourceApi {

    @Inject
    CustomerService customerService;

    @Override
    public List<Customer> getAllCustomers() {
        return customerService.listAllCustomers();
    }

    @Override
    public Response createCustomer(Customer customer) {
        return Response.ok(customerService.insertCustomer(customer)).build();
    }

    @Override
    public Customer getCustomer(Long id) {
        return customerService.findCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Response updateCustomer(Long id, Customer customer) {
        return Response.ok(customerService.updateCustomer(id, customer).orElseThrow(NotFoundException::new)).build();
    }

    @Override
    public Response deleteCustomer(Long id) {
        return Response.ok(customerService.deleteCustomer(id).orElse(id)).build();
    }

    //TODO delete ne doit etre accessible que par les admins et vérifier qu'unes activités ne pointent vers lui
}
