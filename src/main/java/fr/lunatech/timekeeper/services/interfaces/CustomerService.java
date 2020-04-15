package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.services.dtos.CustomerRequest;
import fr.lunatech.timekeeper.services.dtos.CustomerResponse;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<CustomerResponse> findCustomerById(Long id);
    List<CustomerResponse> listAllCustomers();
    Long createCustomer(CustomerRequest request);
    Optional<Long> updateCustomer(Long id, CustomerRequest request);
}