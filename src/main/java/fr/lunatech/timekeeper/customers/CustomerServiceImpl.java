package fr.lunatech.timekeeper.customers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Override
    public Optional<Customer> findCustomerById(Long id) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id).map(this::fromEntity);
    }

    @Override
    public List<Customer> listAllCustomers() {
        try (final Stream<CustomerEntity> users = CustomerEntity.streamAll()) {
            return users.map(this::fromEntity).collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public Long insertCustomer(CustomerMutable customer) {
        final var entity = toEntity(customer);
        CustomerEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    @Override
    public Optional<Long> updateCustomer(Long id, CustomerMutable customer) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id).map(entity -> fillEntity(entity, customer).id);
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

    private CustomerEntity toEntity(CustomerMutable customer) {
        final var entity = new CustomerEntity();
        entity.name = customer.getName();
        entity.description = customer.getDescription();
        return entity;
    }

    private CustomerEntity fillEntity(CustomerEntity entity, CustomerMutable customer) {
        entity.name = customer.getName();
        entity.description = customer.getDescription();
        return entity;
    }

    private Customer fromEntity(CustomerEntity entity) {
        return new Customer(
                entity.id,
                entity.name,
                entity.description,
                entity.activities != null ? entity.activities.stream().map(a -> a.id).collect(Collectors.toList()) : Collections.emptyList()
        );
    }
}