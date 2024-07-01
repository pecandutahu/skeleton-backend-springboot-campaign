package campaignms.campaignms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import campaignms.campaignms.models.EmailMassLog;

public interface EmailMassLogRepository extends JpaRepository<EmailMassLog, Long> {
    
}
