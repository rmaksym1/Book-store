package com.origin.bookstore.service;

import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.model.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto save(User user, OrderRequestDto orderRequestDto);

    Page<OrderResponseDto> getAllOrders(User user, Pageable pageable);

    List<OrderItemResponseDto> getAllOrderItems(User user, Long orderId);

    OrderItemResponseDto getOrderItemById(User user, Long orderId, Long orderItemId);

    OrderResponseDto updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDto updateOrderStatusRequestDto);
}
