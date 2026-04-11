package com.origin.bookstore.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(
        @NotNull(message = "Book id cannot be null")
        @Positive(message = "Book id must be positive")
        Long bookId,
        @Positive(message = "Book quantity must be positive")
        int quantity
) {}

