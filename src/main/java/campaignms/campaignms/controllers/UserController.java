package campaignms.campaignms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import campaignms.campaignms.dto.RegisterUserRequest;
import campaignms.campaignms.dto.WebResponse;
import campaignms.campaignms.models.User;
import campaignms.campaignms.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/")
@Validated
public class UserController {
    
    @Autowired
    UserService userService;

    @PostMapping(
        path = "users",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<String> register(@Valid @RequestBody RegisterUserRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
        path = "users/current",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<User> get(User user) {
        User userResponse = userService.get(user);
        return WebResponse.<User>builder().data(userResponse).build();
    }
} 
