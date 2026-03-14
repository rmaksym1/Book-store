package com.origin.bookstore.dto.orderitem;

public record OrderItemResponseDto(
        Long id,
        Long bookId,
        int quantity
) {}
