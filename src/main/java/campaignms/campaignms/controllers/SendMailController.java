package campaignms.campaignms.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.Customer;
import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.models.User;
import campaignms.campaignms.services.CustomerService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/send-mail")
@Validated
public class SendMailController {
    @Autowired
    private CustomerService customerService;

    @PostMapping("/all")
    public WebResponse<Object> sendMailToAll(User user, @Valid @RequestBody EmailMassLog emailMassLogReq) {
        List<Customer> customers = customerService.findAllCustomersWithFailedEmails();

        for (Customer customer : customers) {
            customerService.addCustomerToQueue(customer, emailMassLogReq.getSubject(), emailMassLogReq.getContent());
        }

        return WebResponse.<Object>builder().data(null).messages("Email sent queue successfully to " + customers.size() + " customers").build();
    }
}
