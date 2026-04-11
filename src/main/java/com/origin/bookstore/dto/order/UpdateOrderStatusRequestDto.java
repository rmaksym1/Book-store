package com.origin.bookstore.dto.order;

import jakarta.validation.constraints.NotEmpty;

public record UpdateOrderStatusRequestDto(
        @NotEmpty(message = "Order status cannot be null")
        String status
) {}
