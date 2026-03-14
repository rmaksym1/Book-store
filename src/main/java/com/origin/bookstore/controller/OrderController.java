package com.origin.bookstore.controller;

import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.model.User;
import com.origin.bookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders managing", description = "Endpoints for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create order", description = "Create order")
    @PostMapping
    public OrderResponseDto createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid OrderRequestDto orderRequestDto
    ) {
        return orderService.save(user, orderRequestDto);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all orders", description = "Get all orders")
    @GetMapping
    public Page<OrderResponseDto> getAllOrders(
            @AuthenticationPrincipal User user, Pageable pageable) {
        return orderService.getAllOrders(user, pageable);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all order items in order",
            description = "Get all order items in order")
    @GetMapping("/{orderId}/items")
    public List<OrderItemResponseDto> getAllOrderItemsInOrder(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId) {
        return orderService.getAllOrderItems(user, orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order item by id", description = "Get order item by id")
    @GetMapping("/{orderId}/items/{id}")
    public OrderItemResponseDto getOrderItemById(
            @AuthenticationPrincipal User user,
            @PathVariable Long orderId,
            @PathVariable(name = "id") Long itemId) {
        return orderService.getOrderItemById(user, orderId, itemId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status by id", description = "Update order status by id")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatusById(
            @PathVariable Long id,
            @RequestBody
            @Valid UpdateOrderStatusRequestDto updateOrderStatusRequestDto) {
        return orderService.updateOrderStatus(id, updateOrderStatusRequestDto);
    }
}
