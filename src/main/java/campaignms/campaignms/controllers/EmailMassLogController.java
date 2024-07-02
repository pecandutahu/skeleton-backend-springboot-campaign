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

import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.services.EmailMassLogService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/email-mass-log")
@Validated
public class EmailMassLogController {
    @Autowired
    private EmailMassLogService emailMassLogService;

    @GetMapping
    public List<EmailMassLog> getEmailMassLogs() {
        return emailMassLogService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailMassLog> getEmailMassLog(@PathVariable("id") Long id) {
        Optional<EmailMassLog> emailMassLog = emailMassLogService.findById(id);
        if (emailMassLog.isPresent()) {
            return ResponseEntity.ok(emailMassLog.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public EmailMassLog creatEmailMassLog(@Valid @RequestBody EmailMassLog emailMassLog) {
        return emailMassLogService.save(emailMassLog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailMassLog> updateEmailLog(@PathVariable Long id, @Valid @RequestBody EmailMassLog emailDetails) {
        return emailMassLogService.findById(id)
                .map(emailLog -> {
                    emailLog.setEmail(emailDetails.getEmail());
                    emailLog.setSubject(emailDetails.getSubject());
                    emailLog.setContent(emailDetails.getContent());
                    return ResponseEntity.ok(emailMassLogService.save(emailLog));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmailLog(@PathVariable Long id) {
        return emailMassLogService.findById(id)
                .map(emailLog -> {
                    emailMassLogService.deleteById(id);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
    
}
