package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    public UserResponseDto toDto(User user);

    public User toModel(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "id", ignore = true)
    void updateUser(UserRegistrationRequestDto userRegistrationRequestDto,
                    @MappingTarget User user);
}
