package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.models.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<Customer> findCustomerById(Long id);
    List<Customer> listAllCustomers() ;
    Long insertCustomer(Customer customer) ;
    Optional<Long> updateCustomer(Long id, Customer customer);
    Optional<Long> deleteCustomer(Long id);
}