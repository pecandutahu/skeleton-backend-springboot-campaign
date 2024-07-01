package campaignms.campaignms.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.repositories.EmailMassLogRepository;
import jakarta.persistence.EntityManager;

@Service
public class EmailMassLogService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EmailMassLogRepository emailMassLogRepository;

    public List<EmailMassLog> findAll() {
        entityManager.unwrap(Session.class).enableFilter("deletedEmailLogFilter");
        return emailMassLogRepository.findAll();
    }

    public Optional<EmailMassLog> findById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedEmailLogFilter");
        return emailMassLogRepository.findById(id);
    }

    public EmailMassLog save(EmailMassLog emailMassLog) {
        return emailMassLogRepository.save(emailMassLog);
    }

    public void deleteById(Long id) {
        emailMassLogRepository.findById(id).ifPresent(emailLog -> {
            emailLog.setDeleted(true);
            emailMassLogRepository.save(emailLog);
        });
    }
    
}
