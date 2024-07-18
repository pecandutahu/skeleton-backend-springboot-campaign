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

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.models.User;
import campaignms.campaignms.services.EmailMassLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/api/email-mass-log")
@Validated
public class EmailMassLogController {
    @Autowired
    private EmailMassLogService emailMassLogService;

    @GetMapping
    public WebResponse<List<EmailMassLog>> getEmailMassLogs(User user) {
        return WebResponse.<List<EmailMassLog>>builder().data(emailMassLogService.findAll()).messages("success").build();
    }

    @GetMapping("/{id}")
    public WebResponse<Optional<EmailMassLog>> getEmailMassLog(User user, @PathVariable("id") Long id) {
        Optional<EmailMassLog> emailMassLog = emailMassLogService.findById(id);
        return WebResponse.<Optional<EmailMassLog>>builder().data(emailMassLog).messages("success").build();
    }

    @PostMapping
    public WebResponse<EmailMassLog> creatEmailMassLog(User user, @Valid @RequestBody EmailMassLog emailMassLog) {
        return WebResponse.<EmailMassLog>builder().data(emailMassLogService.save(emailMassLog)).messages("success").build();
    }

    @PutMapping("/{id}")
    public WebResponse<EmailMassLog> updateEmailLog(User user, @PathVariable Long id, @Valid @RequestBody EmailMassLog emailDetails) {
        Optional<EmailMassLog> emailMassLog = emailMassLogService.findById(id);
        emailMassLog.get().setEmail(emailDetails.getEmail());
        emailMassLog.get().setSubject(emailDetails.getSubject());
        emailMassLog.get().setContent(emailDetails.getContent());

        return WebResponse.<EmailMassLog>builder().data(emailMassLogService.save(emailMassLog.get())).messages("succsess").build();
    }

    @DeleteMapping("/{id}")
    public WebResponse<EmailMassLog> deleteEmailLog(User user, @PathVariable Long id) {
        Optional<EmailMassLog> emailMasLog = emailMassLogService.findById(id);
        emailMassLogService.deleteById(id);
        return WebResponse.<EmailMassLog>builder().data(emailMasLog.get()).messages("Data deleted successfully").build();
    }
    
}
