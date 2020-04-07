package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.model.Customer;

import java.util.Optional;

public interface CustomerService {

    long addCustomer(Customer customer);
    Optional<Customer> getCustomerById(long id);
}