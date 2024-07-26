package campaignms.campaignms.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import campaignms.campaignms.models.Customer;
import campaignms.campaignms.repositories.CustomerRepository;
import jakarta.persistence.EntityManager;

@Service
public class CustomerService {


    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    public void addCustomerToQueue(Customer customer, String subject, String content) {
        String message = customer.getEmail() + "|" + subject + "|" + content;
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }

    public List<Customer> findAll() {
        entityManager.unwrap(Session.class).enableFilter("deletedCustomerFilter");
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedCustomerFilter");
        Optional<Customer> customer = customerRepository.findById(id);
        if(!customer.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Data not Found");
        }
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public void deleteById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedCustomerFilter");
        Customer customer = customerRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found for this id :: " + id));
            customer.setDeleted(true);
            customerRepository.save(customer);
    }
    public List<Customer> findAllCustomersWithFailedEmails() {
        // Implement the logic to fetch customers with failed email statuses
        return customerRepository.findCustomersWithFailedEmails();
    }
    
}
