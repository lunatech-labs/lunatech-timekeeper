package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.model.Activity;
import fr.lunatech.timekeeper.model.Customer;
import fr.lunatech.timekeeper.services.dto.CustomerDto;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    @Transactional
    public long addCustomer(CustomerDto customerDto) {
        Customer customer = from(customerDto);

        Customer.persist(customer);
        return customer.id;
    }

    @Transactional
    public Optional<Long> updateCustomer(long id, CustomerDto customerDto) {
        return getCustomerById(id).map(customerDtoToModify -> {
            customerDtoToModify.setName(customerDto.getName());
            customerDtoToModify.setDescription(customerDto.getDescription());
            customerDtoToModify.setActivitiesId(customerDto.getActivitiesId());

            Customer customer = from(customerDtoToModify);
            Customer.update(customer.id.toString(),customer);

            return customer.id;
        });
    }

    @Override
    public Optional<CustomerDto> getCustomerById(long id) {
        Optional<Customer> customerOptional = Customer.findByIdOptional(id);
        return customerOptional.map(this::from);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = Customer.listAll();
        return customers.stream().map(this::from).collect(Collectors.toList());
    }

    private CustomerDto from(Customer customer){
        return new CustomerDto(
                Optional.of(customer.id), customer.getName(), customer.getDescription(), customer.getActivities().stream().map(a -> a.id).collect(Collectors.toList()));
    }

    private Customer from(CustomerDto customerDto){
        Customer customer = new Customer();

        List<Activity> activities = customerDto.getActivitiesId().stream().map(activityId -> {
            Optional<Activity> a = Activity.findByIdOptional(activityId);
            return a.orElseThrow(() -> new IllegalStateException("One activity's id doesn't match in database"));
        }).collect(Collectors.toList());

        customerDto.getId().map(v -> customer.id = v);
        customer.setName(customerDto.getName());
        customer.setDescription(customerDto.getDescription());
        customer.setActivities(activities);

        return customer;
    }
}