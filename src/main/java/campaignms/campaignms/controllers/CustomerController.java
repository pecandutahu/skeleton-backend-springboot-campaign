package campaignms.campaignms.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.Customer;
import campaignms.campaignms.models.User;
import campaignms.campaignms.services.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public WebResponse<List<Customer>> getAllCustomers(User user) {
        return WebResponse.<List<Customer>>builder().data(customerService.findAll()).messages("success").build();
    }

    @GetMapping("/{id}")
    public WebResponse<Customer> getCustomerById(User user, @PathVariable Long id) {
        Optional<Customer> customer = customerService.findById(id);
        return WebResponse.<Customer>builder().data(customer.get()).messages("success").build();
    }

    @PostMapping
    public WebResponse<Customer> createCustomer(User user, @Valid @RequestBody Customer customer) {
        return WebResponse.<Customer>builder().data(customer).build();
    }

    @PutMapping("/{id}")
    public WebResponse<Customer> updateCustomer(User user, @PathVariable Long id, @Valid @RequestBody Customer customerDetails) {
        Optional<Customer> customer = customerService.findById(id);
        customer.get().setName(customerDetails.getName());
        customer.get().setEmail(customerDetails.getEmail());
        customerService.save(customer.get());
        return WebResponse.<Customer>builder().data(customer.get()).build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<Customer> deleteCustomer(User user, @PathVariable Long id) {
        Customer customer = customerService.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "data Not Found"));
        customerService.deleteById(id);
        return WebResponse.<Customer>builder()
                .data(customer)
                .messages("Data deleted successfully")
                .build();
    }


    
}
