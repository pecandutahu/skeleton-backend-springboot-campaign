package campaignms.campaignms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import campaignms.campaignms.models.EmailMassLog;

public interface EmailMassLogRepository extends JpaRepository<EmailMassLog, Long>, JpaSpecificationExecutor<EmailMassLog> {
    
}
