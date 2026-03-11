package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.exception.RegistrationException;
import com.origin.bookstore.mapper.UserMapper;
import com.origin.bookstore.model.Role;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.role.RoleRepository;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.service.ShoppingCartService;
import com.origin.bookstore.service.UserService;
import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;

    @Override
    @Transactional
    public UserResponseDto save(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + requestDto.getEmail()
                    + " already exists.  ");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(()
                -> new EntityNotFoundException("Role " + Role.RoleName.ROLE_USER + " not found"));
        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);
        shoppingCartService.createShoppingCartForUser(user);

        return userMapper.toDto(savedUser);
    }
}
