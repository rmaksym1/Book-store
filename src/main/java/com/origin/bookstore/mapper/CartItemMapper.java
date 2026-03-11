package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.CartItemResponseDto;
import com.origin.bookstore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemResponseDto toDto(CartItem cartItem);

    @Mapping(source = "bookId", target = "book.id")
    CartItem toEntity(CartItemRequestDto cartItemRequestDto);
}
