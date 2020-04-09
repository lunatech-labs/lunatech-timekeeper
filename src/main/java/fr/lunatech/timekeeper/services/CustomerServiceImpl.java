package fr.lunatech.timekeeper.services;

import fr.lunatech.timekeeper.model.Customer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    @Transactional
    public long addCustomer(Customer customer) {

        Customer.persist(customer);
        return customer.id;
    }

    @Override
    public Optional<Customer> getCustomerById(long id) {
        return Customer.findByIdOptional(id);


    }

    @Override
    public List<Customer> getAllCustomers() {
        if(Customer.count() == 0){
            return new ArrayList<>();
        } else {
            return Customer.findAll().list();
        }
    }
}