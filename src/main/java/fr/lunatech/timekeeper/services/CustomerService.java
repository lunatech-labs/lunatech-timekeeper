package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    long addCustomer(CustomerRequest request);
    Optional<CustomerResponse> getCustomerById(long id);
    List<CustomerResponse> getAllCustomers();
    Optional<Long> updateCustomer(long id, CustomerRequest request);
}