package campaignms.campaignms.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import campaignms.campaignms.models.CampaignInfo;
import campaignms.campaignms.models.User;
import campaignms.campaignms.repositories.CampaignInfoRepository;
import campaignms.campaignms.repositories.UserRepository;
import campaignms.campaignms.security.BCrypt;
import campaignms.campaignms.services.CampaignInfoService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class CampaignControllerTest {
    
    @Autowired
    private CampaignInfoRepository campaignInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampaignInfoService campaignInfoService;

    @BeforeEach
    void setUp() {
        campaignInfoRepository.deleteAll();
        userRepository.deleteAll();
    }

    /** Test Get */

    @Transactional
    @Test
    void testGetCampaignsSuccess() throws Exception{
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        List<CampaignInfo> campaignInfos = campaignInfoRepository.findAll();

        mockMvc.perform(
            get("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<List<CampaignInfo>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals(response.getData().size(), campaignInfos.size());
            assertNull(response.getErrors());
            assertNotNull(response.getMessages());
            
        });
            
    }
    
    @Transactional
    @Test
    void testGetCampaignsUnAuthorized() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<List<CampaignInfo>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            
        });
            
    }

    /* Test Case Get by id */

    @Transactional
    @Test
    void testGetCampaignsByIdSuccess() throws Exception{
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);
        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            get("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNotNull(response.getData());

            Map<String,Object> responseData = response.getData();
            assertEquals(campaignInfo.getCampaignName(), responseData.get("campaignName"));
            assertEquals(campaignInfo.getCampaignContent(), responseData.get("campaignContent"));
            
        });
            
    }

    @Transactional
    @Test
    void testGetCampaignsByIdNotFound() throws Exception{
        
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            get("/api/campaigns/0")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
            
    }
    
    @Transactional
    @Test
    void testGetCampaignsByIdUnauthorized() throws Exception{

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);
        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            get("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
            
    }

    /* Test Case create */
    @Transactional
    @Test
    void testCreateCampaignSuccess() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");

        mockMvc.perform(
            post("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campaignInfo))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNotNull(response.getData());

            Map<String,Object> responseData = response.getData();
            assertEquals(campaignInfo.getCampaignName(), responseData.get("campaignName"));
            assertEquals(campaignInfo.getCampaignContent(), responseData.get("campaignContent"));
            
        });
            
    }

    @Transactional
    @Test
    void testCreateCampaignUnauthorized() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() - 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");

        mockMvc.perform(
            post("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campaignInfo))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
    }

    @Transactional
    @Test
    void testCreateCampaignFailValidationCampaignName() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("");
        campaignInfo.setCampaignContent("Test Content");

        mockMvc.perform(
            post("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campaignInfo))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertNotNull(responseFieldErrors.get("campaignName"));
            
        });
            
    }
    
    @Transactional
    @Test
    void testCreateCampaignFailValidationCampaignContent() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign Name");
        campaignInfo.setCampaignContent("");

        mockMvc.perform(
            post("/api/campaigns")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(campaignInfo))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            assertNotNull(response.getFieldErrors());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertNotNull(responseFieldErrors.get("campaignContent"));
            
        });
            
    }

    /* Test Case Update Campaign */

    @Transactional
    @Test
    void testUpdateCampaignSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);

        CampaignInfo request = new CampaignInfo();
        request.setCampaignName("Test Campaign Updated");
        request.setCampaignContent("Test Content Updated");

        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            put("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNotNull(response.getData());

            Map<String,Object> responseData = response.getData();
            assertEquals("Test Campaign Updated", responseData.get("campaignName"));
            assertEquals("Test Content Updated", responseData.get("campaignContent"));
            
        });
            
    }

    @Transactional
    @Test
    void testUpdateCampaignUnauthorized() throws Exception {

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);

        CampaignInfo request = new CampaignInfo();
        request.setCampaignName("Test Campaign Updated");
        request.setCampaignContent("Test Content Updated");

        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            put("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
            status().isUnauthorized()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
            
    }

    @Transactional
    @Test
    void testUpdateCampaignFailValidationCampaignName() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);

        CampaignInfo request = new CampaignInfo();
        request.setCampaignName("");
        request.setCampaignContent("Test Content Updated");

        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            put("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getFieldErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertNotNull(responseFieldErrors.get("campaignName"));
            
        });
            
    }

    @Transactional
    @Test
    void testUpdateCampaignFailValidationCampaignContent() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);

        CampaignInfo request = new CampaignInfo();
        request.setCampaignName("Test Campaign Updated");
        request.setCampaignContent("");

        Long campaignId = campaignInfo.getCampaignId();

        mockMvc.perform(
            put("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isBadRequest()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getFieldErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());

            Map<String,Object> responseFieldErrors = response.getFieldErrors();
            assertNotNull(responseFieldErrors.get("campaignContent"));
            
        });
            
    }

    @Transactional
    @Test
    void testUpdateCampaignNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo request = new CampaignInfo();
        request.setCampaignName("Test Campaign Updated");
        request.setCampaignContent("Test Content Updated");


        mockMvc.perform(
            put("/api/campaigns/0")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
            
    }

    /* Test Case Delete Campaign */
    @Transactional
    @Test
    void testDeleteCampaignSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        CampaignInfo campaignInfo = new CampaignInfo();
        campaignInfo.setCampaignName("Test Campaign");
        campaignInfo.setCampaignContent("Test Content");
        campaignInfoRepository.save(campaignInfo);
        Long campaignId = campaignInfo.getCampaignId();


        mockMvc.perform(
            delete("/api/campaigns/" + campaignId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isOk()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNotNull(response.getData());

            Map<String, Object> responseData = (Map<String, Object>) response.getData();
            assertTrue((Boolean) responseData.get("deleted"));
            
        });
            
    }
    @Transactional
    @Test
    void testDeleteCampaignNotFound() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000000000L);
        userRepository.save(user);

        mockMvc.perform(
            delete("/api/campaigns/0")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
        ).andExpectAll(
            status().isNotFound()
        ).andDo( result -> {
            WebResponse<Map<String, Object>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNotNull(response.getMessages());
            assertNull(response.getData());
            
        });
            
    }
}
