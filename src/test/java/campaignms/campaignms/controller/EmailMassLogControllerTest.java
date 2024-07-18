package campaignms.campaignms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.EmailMassLog;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.EmailMassLogRepository;
import campaignms.campaignms.repositories.UserRepository;
import campaignms.campaignms.security.BCrypt;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class EmailMassLogControllerTest {

    @Autowired
    EmailMassLogRepository emailMassLogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        emailMassLogRepository.deleteAll();
    }

    /* Test Get all */

    @Transactional
    @Test
    void testGetAllEmailMassLogsSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        List<EmailMassLog> emailMassLogs = emailMassLogRepository.findAll();

        
        mockMvc.perform(
            get("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isOk()
        ).andDo( result -> {
            WebResponse<List<EmailMassLog>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            
            assertNotNull(response.getData());
            assertEquals(response.getData().size(), emailMassLogs.size());
            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
        });
    }

    @Transactional
    @Test
    void testGetAllEmailMassLogsUnauthorized() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<List<EmailMassLog>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            
            assertNull(response.getData());
            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
        });
    }
    @Transactional
    @Test
    void testGetAllEmailMassLogsUnauthorizedNoToken() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<List<EmailMassLog>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            
            assertNull(response.getData());
            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
        });
    }
    

    /* Test Get By Id */

    @Transactional
    @Test
    void testGetByIdEmailMassLogsSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);

        Long emailMasLogId = emailMassLogs.getEmailMasLogId();

        mockMvc.perform(
            get("/api/email-mass-log/" + emailMasLogId)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            assertEquals(emailMassLogs.getSubject(), response.getData().get("subject"));
            assertEquals(emailMassLogs.getContent(), response.getData().get("content"));
            assertEquals(emailMassLogs.getEmail(), response.getData().get("email"));
        });
    }

    @Transactional
    @Test
    void testGetByIdEmailMassLogsFailNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/email-mass-log/0")
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

     /* Test Create */

    @Transactional
    @Test
    void testCreateEmailMassLogsSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");

        mockMvc.perform(
            post("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emailMassLogs))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            assertEquals(emailMassLogs.getSubject(), response.getData().get("subject"));
            assertEquals(emailMassLogs.getContent(), response.getData().get("content"));
            assertEquals(emailMassLogs.getEmail(), response.getData().get("email"));
        });
    }

    @Transactional
    @Test
    void testCreateEmailMassLogsUnAuthorized() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");

        mockMvc.perform(
            post("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emailMassLogs))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

    @Transactional
    @Test
    void testCreateEmailMassLogsFailValidationSubject() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");

        mockMvc.perform(
            post("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emailMassLogs))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            
            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());
            
            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("subject"));

        });
    }

    @Transactional
    @Test
    void testCreateEmailMassLogsFailValidationContent() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("");
        emailMassLogs.setEmail("test@t.co.id");

        mockMvc.perform(
            post("/api/email-mass-log")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emailMassLogs))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            
            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());
            
            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("content"));
        });
    }

    /* Test Update */

    @Transactional
    @Test
    void testUpdateEmailMassLogsSuccess() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            put("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            assertEquals(request.getSubject(), response.getData().get("subject"));
            assertEquals(request.getContent(), response.getData().get("content"));
            assertEquals(request.getEmail(), response.getData().get("email"));
        });
    }

    @Transactional
    @Test
    void testUpdateEmailMassLogsUnauthorized() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            put("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
        });
    }

    @Transactional
    @Test
    void testUpdateEmailMassLogsNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            put("/api/email-mass-log/0")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());

        });
    }

    @Transactional
    @Test
    void testUpdateEmailMassLogsFailValidationSubject() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        EmailMassLog request = new EmailMassLog();
        request.setSubject("");
        request.setContent("Test Updated");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            put("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("subject"));
        });
    }

    @Transactional
    @Test
    void testUpdateEmailMassLogsFailValidationContent() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            put("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertEquals(1, responseFieldErrors.size());
            assertNotNull(responseFieldErrors.get("content"));
        });
    }

    @Transactional
    @Test
    void testDeleteEmailMassLogsSuccess() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        mockMvc.perform(
            delete("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            assertEquals(emailMassLogs.getSubject(), response.getData().get("subject"));
            assertEquals(emailMassLogs.getContent(), response.getData().get("content"));
            assertEquals(emailMassLogs.getEmail(), response.getData().get("email"));
            assertTrue((Boolean) (response.getData().get("deleted")));
        });
    }

    @Transactional
    @Test
    void testDeleteEmailMassLogsUnauthorized() throws Exception {

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        EmailMassLog emailMassLogs = new EmailMassLog();
        emailMassLogs.setSubject("Test");
        emailMassLogs.setContent("Test");
        emailMassLogs.setEmail("test@t.co.id");
        emailMassLogRepository.save(emailMassLogs);
        
        Long emailMassLogId = emailMassLogs.getEmailMasLogId();

        mockMvc.perform(
            delete("/api/email-mass-log/" + emailMassLogId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });

            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
        });
    }

    @Transactional
    @Test
    void testDeleteEmailMassLogsNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("Test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        EmailMassLog request = new EmailMassLog();
        request.setSubject("Test Updated");
        request.setContent("Test Updated");
        request.setEmail("test@t.co.id");

        mockMvc.perform(
            delete("/api/email-mass-log/0")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-API-TOKEN", user.getToken())
        ).andExpect(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){
            });
            assertNotNull(response.getMessages());
            assertNotNull(response.getErrors());
            assertNull(response.getData());

        });
    }

    

     

}
