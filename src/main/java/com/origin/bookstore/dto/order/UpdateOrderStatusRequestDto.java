package com.origin.bookstore.dto.order;

import jakarta.validation.constraints.NotEmpty;

public record UpdateOrderStatusRequestDto(
        @NotEmpty
        String status
) {}
