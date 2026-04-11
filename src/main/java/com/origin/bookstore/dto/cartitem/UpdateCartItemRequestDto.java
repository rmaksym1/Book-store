package com.origin.bookstore.dto.cartitem;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequestDto(
        @Min(value = 1, message = "Quantity must be in minimum of 1")
        int quantity
) {}
