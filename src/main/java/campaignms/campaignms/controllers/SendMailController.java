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
import campaignms.campaignms.services.EmailMassLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/send-mail")
@Validated
public class SendMailController {
    @Autowired
    private EmailMassLogService emailMassLogService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/all")
    public WebResponse<Object> sendMailToAll(User user, @Valid @RequestBody EmailMassLog emailMassLogReq) {
        List<Customer> customers = customerService.findAll();

        List<EmailMassLog> emailMassLogs = customers.stream()
        .map(customer -> {
            EmailMassLog emailMassLog = new EmailMassLog();
            emailMassLog.setEmail(customer.getEmail());
            emailMassLog.setSubject(emailMassLogReq.getSubject());
            emailMassLog.setContent(emailMassLogReq.getContent());
            return emailMassLog;
            
        })
        .collect(Collectors.toList());
        
        emailMassLogService.saveAll(emailMassLogs);
        return WebResponse.<Object>builder().data(emailMassLogs).messages("Email sent successfully to " + customers.size() + " customers").build();
    }
}
