package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.exception.RegistrationException;
import com.origin.bookstore.mapper.UserMapper;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto save(UserRegistrationRequestDto userRegistrationRequestDto) {
        if (userRepository.existsByEmail(userRegistrationRequestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + userRegistrationRequestDto.getEmail()
                    + " already exists.  ");
        }
        User user = userMapper.toModel(userRegistrationRequestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
