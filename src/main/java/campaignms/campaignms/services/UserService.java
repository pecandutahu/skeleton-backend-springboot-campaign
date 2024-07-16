package campaignms.campaignms.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import campaignms.campaignms.exceptions.ResourceNotFoundException;
import campaignms.campaignms.models.RegisterUserRequest;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.UserRepository;
import campaignms.campaignms.security.BCrypt;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void register( RegisterUserRequest request) {
        Optional<User> oldUser = userRepository.findByUsername(request.getUsername());
        if (oldUser.isPresent()) {
            throw new ResourceNotFoundException("Username already exists");
        }else{
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
            user.setName(request.getName());
            userRepository.save(user);

        }

    }

    public User get(User user) {
        return User.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    
}
