package com.origin.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank(message = "Email cannot be null")
        @Size(min = 5, max = 100, message = "Email must be between 5 and 20 characters")
        @Email(message = "Incorrect email format")
        String email,
        @NotBlank(message = "Password cannot be null")
        @Size(min = 8, max = 30, message = "Password must be between 8 and 20 characters")
        String password
) { }
