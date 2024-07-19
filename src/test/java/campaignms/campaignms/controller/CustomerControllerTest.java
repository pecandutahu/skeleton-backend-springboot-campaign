package campaignms.campaignms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import campaignms.campaignms.models.Customer;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.CustomerRepository;
import campaignms.campaignms.repositories.UserRepository;
import campaignms.campaignms.security.BCrypt;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    /** Test for create */
    @Transactional
    @Test
    public void testCreateCustomerSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("test@mail.co.id");

        mockMvc.perform(
            post("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
                WebResponse<Customer> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNull(response.getErrors());
                assertNotNull(response.getMessages());
                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNull(fieldErrors);

                assertNotNull(response.getData());
                assertEquals("test", response.getData().getName());
                assertEquals("test@mail.co.id", response.getData().getEmail());
        });
    }

    @Transactional
    @Test
    public void testCreateCustomerFailAuth() throws Exception {
        Customer customer = new Customer();
        customer.setName("");
        customer.setEmail("test@test.com");

        mockMvc.perform(
            post("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                assertNotNull(response.getErrors());
        });
    }

    @Transactional
    @Test
    public void testCreateCustomerFailValidationName() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("");
        customer.setEmail("test@test.com");

        mockMvc.perform(
            post("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());

                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNotNull(fieldErrors.get("name"));
                assertEquals("Name is mandatory", fieldErrors.get("name"));
        });
    }

    @Transactional
    @Test
    public void testCreateCustomerFailValidationEmail() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("");

        mockMvc.perform(
            post("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());

                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNotNull(fieldErrors.get("email"));
                assertEquals("Email is mandatory", fieldErrors.get("email"));
        });
    }

    @Transactional
    @Test
    public void testCreateCustomerFailValidationEmailFormat() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("wrong email");

        mockMvc.perform(
            post("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());

                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNotNull(fieldErrors.get("email"));
                assertEquals("Email should be valid", fieldErrors.get("email"));
        });
    }

    
    /* Test For Update */
    @Transactional
    @Test
    public void testUpdateCustomerSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer oldCustomer = new Customer();
        oldCustomer.setName("test");
        oldCustomer.setEmail("test@test.com");
        customerRepository.save(oldCustomer);
        Long customerId = oldCustomer.getCustomerId();

        Customer customer = new Customer();
        customer.setName("test updated");
        customer.setEmail("a@mail.com");

        mockMvc.perform(
            put("/api/customers/" + customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
            });
                
            assertNull(response.getErrors());
            assertNotNull(response.getData());

            Map<String, Object> data = (Map<String,Object>) response.getData();

            assertEquals("test updated", data.get("name"));
            assertEquals("a@mail.com", data.get("email"));
        });
    }

    @Transactional
    @Test
    public void testUpdateCustomerFailValidationName() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("");
        customer.setEmail("test@test.com");

        mockMvc.perform(
            put("/api/customers/8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());

                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNotNull(fieldErrors.get("name"));
                assertEquals("Name is mandatory", fieldErrors.get("name"));
        });
    }

    @Transactional
    @Test
    public void testUpdateCustomerFailValidationEmail() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("");

        mockMvc.perform(
            put("/api/customers/8")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());

                Map<String,Object> fieldErrors = (Map<String, Object>) response.getFieldErrors();
                assertNotNull(fieldErrors.get("email"));
                assertEquals("Email is mandatory", fieldErrors.get("email"));
        });
    }

    @Transactional
    @Test
    public void testUpdateCustomerFailNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("a@mail.com");

        mockMvc.perform(
            put("/api/customers/0")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());
                assertNull(response.getData());
        });
    }

    /* Test For Delete */
    @Transactional
    @Test
    public void testDeleteCustomerSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("a@mail.com");
        customerRepository.save(customer);

        Long customerId = customer.getCustomerId();

        mockMvc.perform(
            delete("/api/customers/" + customerId)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
                WebResponse<Object> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNull(response.getErrors());
                assertNotNull(response.getData());
                assertNotNull(response.getMessages());
        });
    }
    
    @Transactional
    @Test
    public void testDeleteCustomerFailNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            delete("/api/customers/0")
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
                WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
                });
                
                assertNotNull(response.getErrors());
                assertNull(response.getData());
        });
    }

    
    /* Test For Get Customers */

    @Transactional
    @Test
    public void testGetCustomersSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        List<Customer> customer = customerRepository.findAll();

        mockMvc.perform(
            get("/api/customers")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<List<Customer>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
            });
            
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertNotNull(response.getMessages());

            List<Customer> data = response.getData();

            assertEquals(customer.size(), data.size() );
        });
    }

    @Transactional
    @Test
    public void testGetCustomerSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        Customer customer = new Customer();
        customer.setName("test");
        customer.setEmail("test@test.com");
        customerRepository.save(customer);
        Long customerId = customer.getCustomerId();

        mockMvc.perform(
            get("/api/customers/" + customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(customer))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
            });
                
            assertNull(response.getErrors());
            assertNotNull(response.getData());
            assertNotNull(response.getMessages());

            Map<String, Object> data = (Map<String,Object>) response.getData();

            assertEquals("test", data.get("name"));
            assertEquals("test@test.com", data.get("email"));
        });
    }

    @Transactional
    @Test
    public void testGetCustomerNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/customers/0")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {            
            });
            
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNotNull(response.getMessages());
        });
    }
}
