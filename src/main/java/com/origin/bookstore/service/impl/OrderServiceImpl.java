package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.OrderItemMapper;
import com.origin.bookstore.mapper.OrderMapper;
import com.origin.bookstore.model.Order;
import com.origin.bookstore.model.OrderItem;
import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.order.OrderRepository;
import com.origin.bookstore.repository.orderitem.OrderItemRepository;
import com.origin.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.origin.bookstore.service.OrderService;
import com.origin.bookstore.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderResponseDto save(User user, OrderRequestDto orderRequestDto) {

        final ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't get shopping cart by user id: "
                        + user.getId())
        );

        Order order = orderMapper.toEntity(orderRequestDto);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        order.setUser(user);
        order.setTotal(BigDecimal.ZERO);
        order.getOrderItems()
                .addAll(shoppingCart
                        .getCartItems().stream()
                        .map(cartItem -> {
                            OrderItem orderItem = orderItemMapper.toOrderItem(cartItem);
                            orderItem.setOrder(order);
                            orderItem.setPrice(cartItem.getBook().getPrice()
                                    .multiply(BigDecimal.valueOf(cartItem
                                            .getQuantity())));
                            order.setTotal(order.getTotal().add(orderItem.getPrice()));
                            return orderItem;
                        })
                        .toList());
        shoppingCartService.clearShoppingCart(user);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponseDto> getAllOrders(User user, Pageable pageable) {
        return orderRepository
                .getOrdersByUser(user, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public List<OrderItemResponseDto> getAllOrderItems(User user, Long orderId) {
        return orderItemRepository
                .findAllByOrderIdAndOrderUser(orderId, user).stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemResponseDto getOrderItemById(
            User user, Long orderId,
            Long orderItemId) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrderIdAndOrderUser(orderItemId, orderId, user)
                .orElseThrow(
                    () -> new EntityNotFoundException("Order item by id: "
                        + orderItemId
                        + " not found")
        );

        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderResponseDto updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequestDto updateOrderStatusRequestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Order by id: "
                        + orderId
                        + " not found")
        );

        orderMapper.updateOrder(updateOrderStatusRequestDto, order);
        return orderMapper.toDto(orderRepository.save(order));
    }
}
