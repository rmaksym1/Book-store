package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(source = "user.id", target = "userId")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
