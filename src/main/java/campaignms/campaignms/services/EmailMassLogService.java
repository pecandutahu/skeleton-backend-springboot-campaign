package campaignms.campaignms.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.repositories.EmailMassLogRepository;
import campaignms.campaignms.spesifications.EmailMassLogSpecification;
import jakarta.persistence.EntityManager;

@Service
public class EmailMassLogService {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EmailMassLogRepository emailMassLogRepository;

    public Page<EmailMassLog> findAllWithFilterAndSort(String sortBy, String sortDirection, String filterColumn, String filterValue, int page, int limit) {
        entityManager.unwrap(Session.class).enableFilter("deletedEmailLogFilter");

        Specification<EmailMassLog> specification = Specification.where(null);

        if (filterColumn != null && !filterColumn.isEmpty() && filterValue != null && !filterValue.isEmpty()) {
            specification = specification.and(EmailMassLogSpecification.containsColumn(filterColumn, filterValue));
        }
        if(sortBy != null && sortDirection != null) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable = PageRequest.of(page, limit, sort);
            return emailMassLogRepository.findAll(specification, pageable);
        }else{
            Pageable pageable = PageRequest.of(page, limit);
            return emailMassLogRepository.findAll(specification, pageable);
        }



    }

    public List<EmailMassLog> findAll() {
        entityManager.unwrap(Session.class).enableFilter("deletedEmailLogFilter");
        return emailMassLogRepository.findAll();
    }

    public Optional<EmailMassLog> findById(Long id) {
        entityManager.unwrap(Session.class).enableFilter("deletedEmailLogFilter");
        Optional<EmailMassLog> emailMassLog = emailMassLogRepository.findById(id);
        if (!emailMassLog.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email Mass Log not found for id: " + id);
        }
        return emailMassLog;
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

    @Transactional
    public void saveAll(List<EmailMassLog> emailMassLogs) {
        emailMassLogRepository.saveAll(emailMassLogs);
    }

    
}
