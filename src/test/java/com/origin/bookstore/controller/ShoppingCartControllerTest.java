package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.CartItemResponseDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.service.ShoppingCartService;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {
    public static final Long CART_ITEM_ID = 4L;
    private static final String ADD_SHOPPINGCART_PATH =
            "/database/shoppingcarts/add-shopping-cart-with-user-to-tables.sql";
    private static final String REMOVE_SHOPPINGCART_PATH =
            "/database/shoppingcarts/remove-shopping-cart-with-user-from-tables.sql";
    public static final String CART_URL = "/cart";
    public static final String CART_ITEMS_CART_ITEM_ID_URL = "/cart/items/{cartItemId}";
    public static final String $_ID = "$.id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should add book to cart")
    void addBookToCart_ValidRequest_ReturnsCreated() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 2);
        ShoppingCartResponseDto responseDto = TestUtil.createShoppingCartResponseDto();
        responseDto.setId(1L);

        when(shoppingCartService.addBookToCart(any(), any())).thenReturn(responseDto);

        mockMvc.perform(post(CART_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath($_ID).exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when saving book for invalid cart")
    void addBookToInvalidCart_ValidRequest_ReturnsNotFound() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 5);
        when(shoppingCartService.addBookToCart(any(), any()))
                .thenThrow(new EntityNotFoundException("Shopping cart by user not found"));

        mockMvc.perform(post(CART_URL)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get items from shopping cart")
    void getCartItems_ReturnsShoppingCart() throws Exception {
        ShoppingCartResponseDto responseDto = TestUtil.createShoppingCartResponseDto();
        CartItemResponseDto cartItemResponseDto = TestUtil.createCartItemResponseDto();
        responseDto.setCartItems(Set.of(cartItemResponseDto));

        when(shoppingCartService.getShoppingCartByUserId(any())).thenReturn(responseDto);

        mockMvc.perform(get(CART_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems[0].bookTitle").value(cartItemResponseDto.bookTitle())
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when cart is not found")
    void getCartItems_CartNotFound_ReturnsNotFound() throws Exception {
        when(shoppingCartService.getShoppingCartByUserId(any()))
                .thenThrow(new EntityNotFoundException("Can't find shopping cart for user"));

        mockMvc.perform(get(CART_URL))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should successfully update book quantity in cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookQuantityInCart_ValidRequest_ReturnsShoppingCart() throws Exception {
        ShoppingCartResponseDto responseDto = TestUtil.createShoppingCartResponseDto();
        UpdateCartItemRequestDto cartItemRequestDto = TestUtil.createUpdateCartItemRequestDto();
        responseDto.setId(5L);

        when(shoppingCartService.updateBookQuantityInCart(any(), eq(CART_ITEM_ID), any()))
                .thenReturn(responseDto);

        mockMvc.perform(put(CART_ITEMS_CART_ITEM_ID_URL, CART_ITEM_ID)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID).exists()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return bad request when updating negative book quantity in cart")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookNegativeQuantityInCart_ReturnsBadRequest() throws Exception {
        ShoppingCartResponseDto responseDto = TestUtil.createShoppingCartResponseDto();
        UpdateCartItemRequestDto cartItemRequestDto = new UpdateCartItemRequestDto(-999);
        responseDto.setId(5L);

        when(shoppingCartService.updateBookQuantityInCart(any(), eq(CART_ITEM_ID), any()))
                .thenReturn(responseDto);

        mockMvc.perform(put(CART_ITEMS_CART_ITEM_ID_URL, CART_ITEM_ID)
                        .content(objectMapper.writeValueAsString(cartItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @WithMockUser(roles = "USER")
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
