package com.origin.bookstore.dto.order;

import jakarta.validation.constraints.NotBlank;

public record OrderRequestDto(
        @NotBlank(message = "Order shipping address cannot be null")
        String shippingAddress
) {}
