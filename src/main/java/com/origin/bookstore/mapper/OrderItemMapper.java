package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.model.CartItem;
import com.origin.bookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemResponseDto toDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "book", source = "book")
    OrderItem toOrderItem(CartItem cartItem);
}
