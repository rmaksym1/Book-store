package com.origin.bookstore.controller;

import com.origin.bookstore.dto.user.UserLoginRequestDto;
import com.origin.bookstore.dto.user.UserLoginResponseDto;
import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.exception.RegistrationException;
import com.origin.bookstore.security.AuthenticationService;
import com.origin.bookstore.service.UserService;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    private static final String REGISTRATION_PATH = "/auth/registration";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String ID_JSON_PATH =
            "$.id";
    private static final String EMAIL_JSON_PATH =
            "$.email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Register a new user successfully")
    void register_ValidRequest_ReturnsUserResponse() throws Exception {
        UserRegistrationRequestDto request = TestUtil.createUserRegistrationRequestDto();
        UserResponseDto expectedResponse = TestUtil.createUserResponseDto();
        expectedResponse.setId(1L);

        when(userService.save(any(UserRegistrationRequestDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(REGISTRATION_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(EMAIL_JSON_PATH).value(expectedResponse.getEmail()))
                .andExpect(jsonPath(ID_JSON_PATH).value(1L));
    }

    @Test
    @DisplayName("Should login user successfully and return token")
    void login_ValidRequest_ReturnsToken() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto("test@example.com", "password");
        UserLoginResponseDto expectedResponse = new UserLoginResponseDto("valid-jwt-token");

        when(authenticationService.authenticate(any(UserLoginRequestDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    @Test
    @DisplayName("Register with invalid data should return bad request")
    void register_InvalidRequest_ReturnsBadRequest() throws Exception {
        UserRegistrationRequestDto invalidRequest = TestUtil.createUserRegistrationRequestDto();
        invalidRequest.setEmail("not an email");
        invalidRequest.setPassword("");

        mockMvc.perform(post(REGISTRATION_PATH)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register with existing email should return conflict")
    void register_DuplicateEmail_ReturnsConflict() throws Exception {
        UserRegistrationRequestDto request = TestUtil.createUserRegistrationRequestDto();

        when(userService.save(any())).thenThrow(new RegistrationException("User already exists"));

        mockMvc.perform(post(REGISTRATION_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}