package campaignms.campaignms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.Customer;
import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.CustomerRepository;
import campaignms.campaignms.repositories.EmailMassLogRepository;
import campaignms.campaignms.repositories.UserRepository;
import campaignms.campaignms.security.BCrypt;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class SendMailControllerTest {
    
    @Autowired
    private EmailMassLogRepository emailMassLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        emailMassLogRepository.deleteAll();
        userRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /* Send Mail ALL Customer Test */
    @Transactional
    @Test
    void testSendMailSuccess() throws Exception {
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        List<Customer> customer = customerRepository.findAll();

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");

        mockMvc.perform(
            post("/api/send-mail/all")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
        )
        .andExpect(status().isOk())
        .andDo( result -> {
            WebResponse<List<Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertEquals(customer.size(), response.getData().size());

        });
    }

    @Transactional
    @Test
    void testSendMailUnAuthorized() throws Exception {
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");

        mockMvc.perform(
            post("/api/send-mail/all")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
        )
        .andExpect(status().isUnauthorized())
        .andDo( result -> {
            WebResponse<List<Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());

        });
    }

    @Transactional
    @Test
    void testSendMailFailValidationSubject() throws Exception {
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog request = new EmailMassLog();
        request.setSubject("");
        request.setContent("Test Updated");

        mockMvc.perform(
            post("/api/send-mail/all")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
        )
        .andExpect(status().isBadRequest())
        .andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());

            Map<String,Object> responseFieldErrors = (Map<String, Object>) response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("subject"));

        });
    }

    @Transactional
    @Test
    void testSendMailFailValidationContent() throws Exception {
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test");
        request.setContent("");

        mockMvc.perform(
            post("/api/send-mail/all")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", "test")
        )
        .andExpect(status().isBadRequest())
        .andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());

            Map<String,Object> responseFieldErrors = (Map<String, Object>) response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("content"));

        });
    }

}
