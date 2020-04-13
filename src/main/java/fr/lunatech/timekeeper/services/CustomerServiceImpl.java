package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.CustomerCreateRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.CustomerUpdateRequest;
import fr.lunatech.timekeeper.models.Customer;
import fr.lunatech.timekeeper.services.interfaces.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

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
    public Long createCustomer(CustomerCreateRequest request) {
        final var customer = new Customer();
        Customer.persist(bind(customer, request));
        return customer.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateCustomer(Long id, CustomerUpdateRequest request) {
        return Customer.<Customer>findByIdOptional(id).map(customer -> bind(customer, request).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteCustomer(Long id) {
        return Customer.<Customer>findByIdOptional(id)
                .map(customer -> {
                    final var oldId = customer.id;
                    if (customer.isPersistent()) {
                        customer.delete();
                    }
                    return oldId;
                });
    }

    public CustomerResponse response(Customer customer) {
        return new CustomerResponse(
                customer.id,
                customer.name,
                customer.description,
                customer.activities.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

    public Customer bind(Customer customer, CustomerCreateRequest request)  {
        customer.name = request.getName();
        customer.description = request.getDescription();
        return customer;
    }

    public Customer bind(Customer customer, CustomerUpdateRequest request)  {
        customer.name = request.getName();
        customer.description = request.getDescription();
        return customer;
    }
}