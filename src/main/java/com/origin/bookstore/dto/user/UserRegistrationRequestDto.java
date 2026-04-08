package com.origin.bookstore.dto.user;

import com.origin.bookstore.validation.FieldMatch;
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
    @NotBlank
    @Size(min = 1, max = 100)
    private String email;
    @NotBlank
    @Size(min = 1, max = 100)
    private String password;
    @NotBlank
    @Size(min = 1, max = 100)
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
