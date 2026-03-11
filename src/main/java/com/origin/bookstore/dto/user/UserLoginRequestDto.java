package com.origin.bookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Size(min = 5, max = 20)
        @Email
        String email,
        @NotBlank
        @Size(min = 3, max = 20)
        String password
) { }
