package fr.lunatech.timekeeper.services.interfaces;

import fr.lunatech.timekeeper.dtos.CustomerResponse;
import fr.lunatech.timekeeper.dtos.CustomerCreateRequest;
import fr.lunatech.timekeeper.dtos.CustomerUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Optional<CustomerResponse> findCustomerById(Long id);
    List<CustomerResponse> listAllCustomers() ;
    Long createCustomer(CustomerCreateRequest request) ;
    Optional<Long> updateCustomer(Long id, CustomerUpdateRequest request);
    Optional<Long> deleteCustomer(Long id);
}