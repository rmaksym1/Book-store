package com.origin.bookstore.dto.cartitem;

import jakarta.validation.constraints.Min;

public record UpdateCartItemRequestDto(
        @Min(1)
        int quantity
) {}
