package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.User.UserRegistrationRequestDto;
import com.origin.bookstore.dto.User.UserResponseDto;
import com.origin.bookstore.mapper.UserMapper;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto) {
        User user = userMapper.toModel(userRegistrationRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
