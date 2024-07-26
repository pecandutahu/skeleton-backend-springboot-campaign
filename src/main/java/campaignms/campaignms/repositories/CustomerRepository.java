package campaignms.campaignms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import campaignms.campaignms.models.Customer;

public interface CustomerRepository extends JpaRepository <Customer, Long> {
    @Query("SELECT c FROM Customer c JOIN EmailMassLog e ON c.email = e.email WHERE e.status = 'FAILED'")
    List<Customer> findCustomersWithFailedEmails();
}
