package com.origin.bookstore.service;

import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto addBookToCart(User user, CartItemRequestDto cartItemRequestDto);

    ShoppingCartResponseDto getShoppingCartByUserId(User user);

    void createShoppingCartForUser(User user);

    ShoppingCartResponseDto updateBookQuantityInCart(
            User user,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartItemRequestDto);

    void deleteBookFromCart(User user, Long cartItemId);
}
