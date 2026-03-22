package com.origin.bookstore.dto.shoppingcart;

import com.origin.bookstore.dto.cartitem.CartItemResponseDto;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartResponseDto {
    private Long id;
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
