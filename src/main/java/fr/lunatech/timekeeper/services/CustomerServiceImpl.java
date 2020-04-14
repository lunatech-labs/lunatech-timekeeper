package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.models.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    @Transactional
    public long addCustomer(CustomerRequest request) {
        Customer customer = from(request);

        Customer.persist(customer);
        return customer.id;
    }

    @Transactional
    public Optional<Long> updateCustomer(long id, CustomerRequest request) {
        return Customer.<Customer>findByIdOptional(id).map(customer -> {
            Customer.update(
                    "name=?1, description=?2 where id=?3",
                    from(request).name,
                    from(request).description,
                    customer.id
            );
            return customer.id;
        });
    }

    @Override
    public Optional<CustomerResponse> getCustomerById(long id) {
        Optional<Customer> customerOptional = Customer.findByIdOptional(id);
        return customerOptional.map(this::from);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers = Customer.listAll();
        return customers.stream().map(this::from).collect(Collectors.toList());
    }

    private CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.id, customer.name, customer.description, customer.activities.stream().map(a -> a.id).collect(Collectors.toList()));
    }

    private Customer from(CustomerRequest request) {
        Customer customer = new Customer();

        customer.name = request.getName();
        customer.description = request.getDescription();

        return customer;
    }
}