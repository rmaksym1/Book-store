package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderResponseDto toDto(Order order);

    Order toEntity(OrderRequestDto orderRequestDto);

    void updateOrder(UpdateOrderStatusRequestDto statusRequestDto, @MappingTarget Order order);
}
