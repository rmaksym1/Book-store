package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
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
public class ShoppingCartControllerTest {
    public static final Long CART_ITEM_ID = 3L;
    public static final String $_ID = "$.id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should add book to cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addBookToCart_ValidRequest_ReturnsCreated() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(2L, 2);

        mockMvc.perform(post(CART_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath($_ID).exists());
    }

    @Test
    @WithUserDetails("rudycooper@gmail.com")
    @DisplayName("Should return 404 when saving book for invalid cart")
    @Sql(scripts = ADD_USER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_USERS_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void addBookToInvalidCart_ValidRequest_ReturnsNotFound() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 5);

        mockMvc.perform(post(CART_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should get items from shopping cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getCartItems_ReturnsShoppingCart() throws Exception {
        mockMvc.perform(get(CART_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].bookTitle").value("name")
                );
    }

    @Test
    @WithUserDetails("rudycooper@gmail.com")
    @DisplayName("Should return 404 when cart is not found")
    @Sql(scripts = ADD_USER_PATH, executionPhase =
            Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts = REMOVE_USERS_PATH, executionPhase =
            Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void getCartItems_CartNotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(CART_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should successfully update book quantity in cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookQuantityInCart_ValidRequest_ReturnsShoppingCart() throws Exception {
        UpdateCartItemRequestDto cartItemRequestDto = TestUtil.createUpdateCartItemRequestDto();

        mockMvc.perform(put(CART_ITEMS_CART_ITEM_ID_URL, CART_ITEM_ID)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).exists()
                );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should return bad request when updating negative book quantity in cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookNegativeQuantityInCart_ReturnsBadRequest() throws Exception {
        UpdateCartItemRequestDto cartItemRequestDto = new UpdateCartItemRequestDto(-999);

        mockMvc.perform(put(CART_ITEMS_CART_ITEM_ID_URL, CART_ITEM_ID)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @WithUserDetails("user@gmail.com")
    @DisplayName("Should delete a book from shopping cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookFromCart_ValidRequest_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(CART_ITEMS_CART_ITEM_ID_URL, CART_ITEM_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()
                );
    }
}
