package com.origin.bookstore.service;

import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.exception.RegistrationException;
import com.origin.bookstore.mapper.UserMapper;
import com.origin.bookstore.model.Role;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.role.RoleRepository;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.service.impl.UserServiceImpl;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ShoppingCartService shoppingCartService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should successfully register a new user")
    void register_ValidRequest_ReturnsUserResponseDto() {
        UserRegistrationRequestDto requestDto = TestUtil.createUserRegistrationRequestDto();
        User user = TestUtil.createUser();
        Role role = new Role();
        UserResponseDto expectedDto = TestUtil.createUserResponseDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto actualDto = userService.save(requestDto);

        assertNotNull(actualDto);
        assertEquals(requestDto.getEmail(), actualDto.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertTrue(user.getRoles().contains(role));
        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(passwordEncoder).encode(anyString());
        verify(roleRepository).findByName(Role.RoleName.ROLE_USER);
        verify(userRepository).save(user);
        verify(shoppingCartService).createShoppingCartForUser(user);
    }

    @Test
    @DisplayName("Should throw exception when the e‑mail already exists")
    void register_DuplicateEmail_ThrowsRegistrationException() {
        UserRegistrationRequestDto requestDto = TestUtil.createUserRegistrationRequestDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        RegistrationException ex =
                assertThrows(RegistrationException.class, () -> userService.save(requestDto));

        assertEquals(
                "User with email " + requestDto.getEmail() + " already exists.  ",
                ex.getMessage());

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper,
                passwordEncoder, roleRepository, shoppingCartService);
    }

    @Test
    @DisplayName("Should throw exception when ROLE_USER is not found")
    void registerWith_InvalidRole_ThrowsException() {
        UserRegistrationRequestDto requestDto = TestUtil.createUserRegistrationRequestDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(TestUtil.createUser());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName(Role.RoleName.ROLE_USER))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> userService.save(requestDto));

        assertEquals(
                "Role " + Role.RoleName.ROLE_USER + " not found",
                ex.getMessage());

        verify(userRepository).existsByEmail(requestDto.getEmail());
        verify(roleRepository).findByName(Role.RoleName.ROLE_USER);
        verifyNoMoreInteractions(userRepository, userMapper,
                passwordEncoder, roleRepository, shoppingCartService);
    }
}