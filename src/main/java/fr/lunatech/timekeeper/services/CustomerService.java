package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.services.dto.CustomerDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    long addCustomer(CustomerDto customerDto);
    Optional<CustomerDto> getCustomerById(long id);
    List<CustomerDto> getAllCustomers();
    Optional<Long> updateCustomer(long id, CustomerDto customerDto);
}