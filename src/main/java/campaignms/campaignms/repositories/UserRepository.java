package campaignms.campaignms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import campaignms.campaignms.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByToken(String token);
}
