package com.origin.bookstore.service;

import com.origin.bookstore.dto.User.UserRegistrationRequestDto;
import com.origin.bookstore.dto.User.UserResponseDto;

public interface UserService {
    public UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto);
}
