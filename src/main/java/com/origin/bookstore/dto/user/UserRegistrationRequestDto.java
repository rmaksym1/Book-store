package com.origin.bookstore.dto.user;

import com.origin.bookstore.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@FieldMatch(
        field = "password",
        fieldToMatch = "repeatPassword"
)
public class UserRegistrationRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;
    @NotBlank(message = "Repeat password is required")
    @Size(min = 8, max = 30, message = "Repeat password must be between 8 and 30 characters")
    private String repeatPassword;
    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name must be less than 255 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 255, message = "Last name must be less than 255 characters")
    private String lastName;
    @Size(max = 500, message = "Shipping address must be less than 500 characters")
    private String shippingAddress;
}
