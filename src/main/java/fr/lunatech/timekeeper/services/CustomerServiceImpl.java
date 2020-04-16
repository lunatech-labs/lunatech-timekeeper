package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.dtos.CustomerRequest;
import fr.lunatech.timekeeper.services.dtos.CustomerResponse;
import fr.lunatech.timekeeper.models.Customer;
import fr.lunatech.timekeeper.services.interfaces.CustomerService;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Optional<CustomerResponse> findCustomerById(Long id) {
        return Customer.<Customer>findByIdOptional(id).map(this::response);
    }

    @Override
    public List<CustomerResponse> listAllCustomers() {
        try (final Stream<Customer> customers = Customer.streamAll()) {
            return customers.map(this::response).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long createCustomer(CustomerRequest request) {
        final var customer = bind(request);
        Customer.persist(customer);
        return customer.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateCustomer(Long id, CustomerRequest request) {
        return Customer.<Customer>findByIdOptional(id).map(customer -> bind(customer, request).id);
    }

    private CustomerResponse response(Customer customer) {
        return new CustomerResponse(
                customer.id,
                customer.name,
                customer.description,
                customer.activities.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    private Customer bind(Customer customer, CustomerRequest request) {
        customer.name = request.getName();
        customer.description = request.getDescription();
        return customer;
    }

    private Customer bind(CustomerRequest request) {
        return bind(new Customer(), request);
    }
}