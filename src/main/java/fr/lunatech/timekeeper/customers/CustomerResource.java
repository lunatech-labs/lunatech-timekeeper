package fr.lunatech.timekeeper.customers;

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
        return Response.ok(customerService.deleteCustomer(id)).build();
    }
}
