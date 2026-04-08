package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.service.OrderService;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    public static final String $_TOTAL_ELEMENTS = "$.totalElements";
    public static final String $_CONTENT = "$.content";
    public static final String $_ID = "$.id";
    public static final String $_QUANTITY = "$.quantity";
    public static final String ORDER_ID_ITEMS_ID_URL = "/orders/{orderId}/items/{id}";
    public static final String ORDERS_ORDER_ID_ITEMS_URL = "/orders/{orderId}/items";
    public static final String ORDERS_ID_URL = "/orders/{id}";
    public static final String ORDERS_URL = "/orders";
    public static final String $_STATUS = "$.status";
    public static final String $_TOTAL = "$.total";
    private final Pageable pageable = PageRequest.of(0, 10);
    private static final Long ID = 1L;
    private static final Long INCORRECT_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should successfully create an order")
    void createOrder_ValidRequest_ReturnsOrderResponseDto() throws Exception {
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();

        OrderResponseDto expectedResponse = TestUtil.createOrderResponseDto();
        expectedResponse.setId(ID);
        expectedResponse.setUserId(5L);
        expectedResponse.setStatus("PENDING");
        expectedResponse.setTotal(BigDecimal.valueOf(100));

        when(orderService.save(any(), any()))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post(ORDERS_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(expectedResponse.getId()))
                .andExpect(jsonPath($_STATUS).value(expectedResponse.getStatus()))
                .andExpect(jsonPath($_TOTAL).value(expectedResponse.getTotal()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 if shopping cart not found while creating order")
    void createOrderWithIncorrectCart_ValidRequest_ReturnsNotFound() throws Exception {
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();

        OrderResponseDto expectedResponse = TestUtil.createOrderResponseDto();
        expectedResponse.setId(ID);
        expectedResponse.setUserId(3L);
        expectedResponse.setStatus("PENDING");
        expectedResponse.setTotal(BigDecimal.valueOf(49.99));

        when(orderService.save(any(), any()))
                .thenThrow(new EntityNotFoundException("Shopping cart by user id: " + expectedResponse.getUserId() + " not found!"));

        // When & Then
        mockMvc.perform(post(ORDERS_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return a list of orders by user")
    void getAllOrders_ValidRequest_ReturnsOrderList() throws Exception {
        OrderResponseDto responseDto = TestUtil.createOrderResponseDto();
        responseDto.setId(ID);
        responseDto.setStatus("COMPLETED");

        List<OrderResponseDto> orderList = List.of(responseDto);
        Page<OrderResponseDto> orderPage = new PageImpl<>(orderList, pageable, orderList.size());

        when(orderService.getAllOrders(any(), any(Pageable.class))).thenReturn(orderPage);

        mockMvc.perform(get(ORDERS_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_CONTENT).isArray())
                .andExpect(jsonPath("$.content[0].id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath($_TOTAL_ELEMENTS).value(1)
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return a list of order items")
    void getAllOrderItemsInOrder_ValidRequest_ReturnsOrderItemsList() throws Exception {
        OrderItemResponseDto orderItemResponseDto = TestUtil.createOrderItemResponseDto();
        List<OrderItemResponseDto> dtos = List.of(orderItemResponseDto);
        Long orderId = ID;

        when(orderService.getAllOrderItems(any(), eq(orderId))).thenReturn(dtos);

        mockMvc.perform(get(ORDERS_ORDER_ID_ITEMS_URL, orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(orderId));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 if order is not found")
    void getAllOrderItemsInIncorrectOrder_ValidRequest_ReturnsNotFound() throws Exception {

        when(orderService.getAllOrderItems(any(), eq(INCORRECT_ID))).thenThrow(new EntityNotFoundException("Order is not found"));

        mockMvc.perform(get(ORDERS_ORDER_ID_ITEMS_URL, INCORRECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should successfully get order item by id")
    void getOrderItemById_ValidRequest_ReturnsOrderItem() throws Exception {
        Long itemId = 2L;
        OrderItemResponseDto responseDto = TestUtil.createOrderItemResponseDto();

        when(orderService.getOrderItemById(any(), eq(ID), eq(itemId)))
                .thenReturn(responseDto);

        mockMvc.perform(get(ORDER_ID_ITEMS_ID_URL, ID, itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(ID))
                .andExpect(jsonPath($_QUANTITY).value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when order item id is incorrect")
    void getOrderItemByIncorrectId_ValidRequest_ReturnsNotFound() throws Exception {

        when(orderService.getOrderItemById(any(), eq(ID), eq(INCORRECT_ID)))
                .thenThrow(new EntityNotFoundException("Order item not found"));

        mockMvc.perform(get(ORDER_ID_ITEMS_ID_URL, ID, INCORRECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should successfully update order status")
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrder() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        OrderResponseDto responseDto = TestUtil.createOrderResponseDto();
        responseDto.setId(ID);
        responseDto.setStatus("DELIVERED");

        when(orderService.updateOrderStatus(eq(ID), any(UpdateOrderStatusRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch(ORDERS_ID_URL, ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 if order not found")
    void updateInvalidOrderStatus_ValidRequest_ReturnsNotFound() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        OrderResponseDto responseDto = TestUtil.createOrderResponseDto();
        responseDto.setId(ID);
        responseDto.setStatus("DELIVERED");

        when(orderService.updateOrderStatus(eq(ID), any(UpdateOrderStatusRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(patch(ORDERS_ID_URL, ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return forbidden when updating order status as user")
    void updateOrderStatusByUser_ValidRequest_ReturnsForbidden() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        OrderResponseDto responseDto = TestUtil.createOrderResponseDto();
        responseDto.setId(ID);
        responseDto.setStatus("PENDING");

        when(orderService.updateOrderStatus(eq(ID), any(UpdateOrderStatusRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch(ORDERS_ID_URL, ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()
                );
    }
}
