package campaignms.campaignms.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import campaignms.campaignms.dto.LoginUserRequest;
import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.TokenResponse;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.UserRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginFailedUserNotFound() throws Exception {
        
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("testttt");

        mockMvc.perform(
            post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void loginSuccess() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
            post("/api/auth/login")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            Optional<User> userDb = userRepository.findByUsername("test");
            assertNotNull(userDb);

            // assertEquals(userDb.getToken(), response.getData().getToken());
            // assertEquals(userDb.getTokenExpiredAt(), response.getData().getExpiredAt());
        });

    }
}
