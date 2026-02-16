package com.origin.bookstore.service;

import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;

public interface UserService {
    public UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto);
}
