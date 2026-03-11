package com.origin.bookstore.dto.shoppingcart;

import com.origin.bookstore.dto.cartitem.CartItemResponseDto;
import java.util.Set;

public record ShoppingCartResponseDto(
        Long id,
        Long userId,
        Set<CartItemResponseDto> cartItems
) {}
