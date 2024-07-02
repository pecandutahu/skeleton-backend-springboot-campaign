package campaignms.campaignms.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import campaignms.campaignms.exceptions.ResourceNotFoundException;
import campaignms.campaignms.models.Customer;
import campaignms.campaignms.repositories.CustomerRepository;
import jakarta.persistence.EntityManager;

@Service
public class CustomerService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        entityManager.unwrap(Session.class).enableFilter("deletedCustomerFilter");
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedCustomerFilter");
        Optional<Customer> customer = customerRepository.findById(id);
        if(!customer.isPresent()){
            throw new ResourceNotFoundException("Customer not found");
        }
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteById(Long id) {
        customerRepository.findById(id).ifPresent(customer -> {
            customer.setDeleted(true);
            customerRepository.save(customer);
        });
    }
    
}
