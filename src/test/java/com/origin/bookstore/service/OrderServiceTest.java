package com.origin.bookstore.service;

import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.OrderItemMapper;
import com.origin.bookstore.mapper.OrderMapper;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.CartItem;
import com.origin.bookstore.model.Order;
import com.origin.bookstore.model.OrderItem;
import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.order.OrderRepository;
import com.origin.bookstore.repository.orderitem.OrderItemRepository;
import com.origin.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.origin.bookstore.service.impl.OrderServiceImpl;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static com.origin.bookstore.util.TestConstants.pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("Should successfully save an order and clear shopping cart")
    void save_ValidOrder_ReturnsOrderResponseDto() {
        User user = TestUtil.createUser();
        Book book = TestUtil.createBook();
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        Order order = TestUtil.createOrder();
        OrderResponseDto expectedDto = TestUtil.createOrderResponseDto();
        CartItem cartItem = TestUtil.createCartItem();
        OrderItem orderItem = TestUtil.createOrderItem();
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);

        cartItem.setBook(book);
        shoppingCart.setCartItems(Set.of(cartItem));
        order.setOrderItems(orderItems);

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(orderMapper.toEntity(requestDto)).thenReturn(order);
        when(orderItemMapper.toOrderItem(any(CartItem.class))).thenReturn(orderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(expectedDto);

        OrderResponseDto actualDto = orderService.save(user, requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);

        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartService).clearShoppingCart(user);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDto(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when shopping cart not found")
    void save_InvalidShoppingCart_ThrowsException() {
        User user = TestUtil.createUser();
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.save(user, requestDto));

        verify(shoppingCartRepository).findByUser(user);
        verifyNoInteractions(orderMapper, orderItemMapper, orderRepository, shoppingCartService);
    }

    @Test
    @DisplayName("Should return a list of orders")
    void getAll_ValidOrders_ReturnsOrdersList() {
        User user = TestUtil.createUser();
        Order order = TestUtil.createOrder();
        OrderResponseDto orderDto = TestUtil.createOrderResponseDto();

        Page<Order> orderList = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.getOrdersByUser(user, pageable)).thenReturn(orderList);
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        orderService.getAllOrders(user, pageable);

        verify(orderRepository).getOrdersByUser(user, pageable);
        verify(orderMapper).toDto(order);
    }

    @Test
    @DisplayName("Should return a list of ordersItems by orderId")
    void getAll_ValidOrdersItemsById_ReturnsOrdersItemsList() {
        Long orderId = 7L;
        User user = TestUtil.createUser();
        user.setId(1L);

        Order order = new Order();
        order.setId(orderId);
        order.setUser(user);

        OrderItem orderItem = TestUtil.createOrderItem();
        OrderItemResponseDto orderItemResponseDto = TestUtil.createOrderItemResponseDto();
        List<OrderItem> orderItems = List.of(orderItem);

        when(orderRepository.findByIdAndUser(orderId, user)).thenReturn(Optional.of(order));
        when(orderItemRepository.findAllByOrderIdAndOrderUser(orderId, user)).thenReturn(orderItems);
        when(orderItemMapper.toDto(orderItem)).thenReturn(orderItemResponseDto);

        List<OrderItemResponseDto> result = orderService.getAllOrderItems(user, orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderItemRepository).findAllByOrderIdAndOrderUser(orderId, user);
        verify(orderItemMapper).toDto(orderItem);
    }

    @Test
    @DisplayName("Should return order item when data is valid")
    void getOrderItemById_ValidData_ReturnsDto() {
        User user = TestUtil.createUser();
        Long orderId = 1L;
        Long orderItemId = 1L;
        OrderItem orderItem = TestUtil.createOrderItem();
        OrderItemResponseDto expectedDto = TestUtil.createOrderItemResponseDto();

        when(orderItemRepository.findByIdAndOrderIdAndOrderUser(orderItemId, orderId, user))
                .thenReturn(Optional.of(orderItem));
        when(orderItemMapper.toDto(orderItem)).thenReturn(expectedDto);

        OrderItemResponseDto actualDto = orderService.getOrderItemById(user, orderId, orderItemId);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(orderItemRepository).findByIdAndOrderIdAndOrderUser(orderItemId, orderId, user);
    }

    @Test
    @DisplayName("Should throw exception if order item not found")
    void getOrderItemById_InvalidId_ThrowsException() {
        User user = TestUtil.createUser();
        Long orderId = 1L;
        Long orderItemId = 1L;

        when(orderItemRepository.findByIdAndOrderIdAndOrderUser(orderItemId, orderId, user))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.getOrderItemById(user, orderId, orderItemId));
        verifyNoInteractions(orderItemMapper);
    }

    @Test
    @DisplayName("Should update order status if order exists")
    void updateOrderStatus_ValidOrder_ReturnsUpdatedDto() {
        Long orderId = 1L;
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();
        Order order = TestUtil.createOrder();
        OrderResponseDto expectedDto = TestUtil.createOrderResponseDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expectedDto);

        OrderResponseDto actualDto = orderService.updateOrderStatus(orderId, requestDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        verify(orderMapper).updateOrder(requestDto, order);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent order")
    void updateOrderStatus_InvalidOrderId_ThrowsException() {
        Long orderId = 1L;
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> orderService.updateOrderStatus(orderId, requestDto));
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(orderMapper);
    }
}
