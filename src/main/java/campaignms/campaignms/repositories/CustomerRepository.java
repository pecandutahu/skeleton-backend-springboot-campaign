package campaignms.campaignms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import campaignms.campaignms.models.Customer;

public interface CustomerRepository extends JpaRepository <Customer, Long> {
    
}
