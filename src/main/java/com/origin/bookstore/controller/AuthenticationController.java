package com.origin.bookstore.controller;

import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User management", description = "Endpoints for managing user")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("/registration")
    @Operation(summary = "Create new user", description = "Create a new user")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request) {
        return userService.save(request);
    }
}
