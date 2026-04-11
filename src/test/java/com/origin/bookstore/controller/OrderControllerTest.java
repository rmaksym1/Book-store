package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static com.origin.bookstore.util.TestConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    private static final Long ID = 10L;
    private static final Long INCORRECT_ID = 999L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should successfully create an order")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_SHOPPINGCART_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void createOrder_ValidRequest_ReturnsOrderResponseDto() throws Exception {
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();

        mockMvc.perform(post(ORDERS_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath($_ID).value(1L))
                .andExpect(jsonPath($_STATUS).value("PENDING"))
                .andExpect(jsonPath($_TOTAL).value(260));
    }

    @Test
    @WithUserDetails("rudycooper@gmail.com")
    @DisplayName("Should return 404 if shopping cart not found while creating order")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_USER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void createOrderWithIncorrectCart_ValidRequest_ReturnsNotFound() throws Exception {
        OrderRequestDto requestDto = TestUtil.createOrderRequestDto();

        mockMvc.perform(post(ORDERS_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return a list of orders by user")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getAllOrders_ValidRequest_ReturnsOrderList() throws Exception {
        mockMvc.perform(get(ORDERS_URL)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_CONTENT).isArray())
                .andExpect(jsonPath("$.content[0].id").value(ID))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath($_TOTAL_ELEMENTS).value(1)
        );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return a list of order items")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getAllOrderItemsInOrder_ValidRequest_ReturnsOrderItemsList() throws Exception {
        Long orderId = ID;

        mockMvc.perform(get(ORDERS_ORDER_ID_ITEMS_URL, orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(orderId));
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return 404 if order is not found")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getAllOrderItemsInIncorrectOrder_ValidRequest_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(ORDERS_ORDER_ID_ITEMS_URL, INCORRECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should successfully get order item by id")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getOrderItemById_ValidRequest_ReturnsOrderItem() throws Exception {
        mockMvc.perform(get(ORDER_ID_ITEMS_ID_URL, ID, ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).value(ID))
                .andExpect(jsonPath($_QUANTITY).value(1));
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return 404 when order item id is incorrect")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void getOrderItemByIncorrectId_ValidRequest_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(ORDER_ID_ITEMS_ID_URL, ID, INCORRECT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    @DisplayName("Should successfully update order status")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ADMIN_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrder() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        mockMvc.perform(patch(ORDERS_ID_URL, ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @WithUserDetails("admin@gmail.com")
    @DisplayName("Should return 404 if order not found while updating status")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ADMIN_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateInvalidOrderStatus_ValidRequest_ReturnsNotFound() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        mockMvc.perform(patch(ORDERS_ID_URL, INCORRECT_ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return forbidden when updating order status as user")
    @Sql(scripts = CLEANUP_DB_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = ADD_ORDER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void updateOrderStatusByUser_ValidRequest_ReturnsForbidden() throws Exception {
        UpdateOrderStatusRequestDto requestDto = TestUtil.createUpdateOrderStatusRequestDto();

        mockMvc.perform(patch(ORDERS_ID_URL, ID)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()
                );
    }
}
