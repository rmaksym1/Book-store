package com.origin.bookstore.controller;

import com.origin.bookstore.dto.user.UserLoginRequestDto;
import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.notNullValue;
import static com.origin.bookstore.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {
    private static final String EMAIL_JSON_PATH =
            "$.email";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Register a new user successfully")
    void register_ValidRequest_ReturnsUserResponse() throws Exception {
        UserRegistrationRequestDto request = TestUtil.createUserRegistrationRequestDto();

        mockMvc.perform(post(REGISTRATION_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(EMAIL_JSON_PATH).value(request.getEmail())
                );
    }

    @Test
    @DisplayName("Should login user successfully and return token")
    @Sql(scripts = ADD_USER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_USERS_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void login_ValidRequest_ReturnsToken() throws Exception {
        UserLoginRequestDto request = new UserLoginRequestDto("rudycooper@gmail.com", "example");

        mockMvc.perform(post(LOGIN_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(notNullValue())
                );
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
    @Sql(scripts = ADD_USER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_USERS_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void register_DuplicateEmail_ReturnsConflict() throws Exception {
        UserRegistrationRequestDto request = TestUtil.createUserRegistrationRequestDto();
        request.setEmail("rudycooper@gmail.com");

        mockMvc.perform(post(REGISTRATION_PATH)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}