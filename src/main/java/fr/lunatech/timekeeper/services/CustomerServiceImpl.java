package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.entities.CustomerEntity;
import fr.lunatech.timekeeper.models.Customer;
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
    public Optional<Customer> findCustomerById(Long id) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id).map(Customer::new);
    }

    @Override
    public List<Customer> listAllCustomers() {
        try (final Stream<CustomerEntity> customers = CustomerEntity.streamAll()) {
            return customers.map(Customer::new).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertCustomer(Customer customer) {
        final var entity = new CustomerEntity();
        CustomerEntity.persist(bind(entity, customer));
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateCustomer(Long id, Customer customer) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id).map(entity -> bind(entity, customer).id);
    }

    @Transactional
    @Override
    public Optional<Long> deleteCustomer(Long id) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id)
                .map(entity -> {
                    final var oldId = entity.id;
                    if (entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    public static CustomerEntity bind(CustomerEntity entity, Customer customer)  {
        entity.name = customer.getName();
        entity.description = customer.getDescription();
        return entity;
    }
}