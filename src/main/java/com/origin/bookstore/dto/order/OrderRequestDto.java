package com.origin.bookstore.dto.order;

import jakarta.validation.constraints.NotEmpty;

public record OrderRequestDto(
        @NotEmpty
        String shippingAddress
) {}
