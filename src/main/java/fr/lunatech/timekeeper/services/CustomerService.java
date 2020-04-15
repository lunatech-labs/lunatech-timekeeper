package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.dtos.CustomerRequest;
import fr.lunatech.timekeeper.dtos.CustomerResponse;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<CustomerResponse> findCustomerById(Long id);
    List<CustomerResponse> listAllCustomers();
    Long createCustomer(CustomerRequest request);
    Optional<Long> updateCustomer(Long id, CustomerRequest request);

}