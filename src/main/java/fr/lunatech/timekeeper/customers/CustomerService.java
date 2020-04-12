package fr.lunatech.timekeeper.customers;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findCustomerById(Long id);
    List<Customer> listAllCustomers() ;
    Long insertCustomer(CustomerMutable customer) ;
    Optional<Long> updateCustomer(Long id, CustomerMutable customer);
    Optional<Long> deleteCustomer(Long id);
}