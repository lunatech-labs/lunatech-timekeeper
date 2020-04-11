package fr.lunatech.timekeeper.customers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CustomerService {

    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    Optional<Customer> findCustomerById(Long id) {
        final Optional<CustomerEntity> entity = CustomerEntity.findByIdOptional(id);
        return entity.map(this::fromEntity);
    }

    List<Customer> listAllCustomers() {
        try (final Stream<CustomerEntity> users = CustomerEntity.streamAll()) {
            return users.map(this::fromEntity)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    Long insertCustomer(Customer customer) {
        final CustomerEntity entity = toEntity(customer);
        CustomerEntity.persist(entity);
        return entity.id;
    }

    @Transactional
    Optional<Long> updateCustomer(Long id, Customer customer) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id)
                .map(entity -> fillEntity(entity, customer).id);
    }

    @Transactional
    Optional<Long> deleteCustomer(Long id) {
        return CustomerEntity.<CustomerEntity>findByIdOptional(id)
                .map(entity -> {
                    final Long oldId = entity.id;
                    if(entity.isPersistent()) {
                        entity.delete();
                    }
                    return oldId;
                });
    }

    private CustomerEntity toEntity(Customer customer) {
        final CustomerEntity entity = new CustomerEntity();
        entity.id = customer.getId().orElse(null);
        entity.name = customer.getName();
        entity.description = customer.getDescription();
        //entity.activities = customer;
        return entity;
    }

    private CustomerEntity fillEntity(CustomerEntity entity, Customer customer) {
        entity.name = customer.getName();
        entity.description = customer.getDescription();
        //entity.activities = customer;
        return entity;
    }

    private Customer fromEntity(CustomerEntity entity) {
        return new Customer(
                entity.id,
                entity.name,
                entity.description,
                entity.activities.stream().map(a -> a.id).collect(Collectors.toList())
        );
    }

}