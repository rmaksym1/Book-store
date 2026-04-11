package com.origin.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequestDto(
        @NotBlank(message = "Category name cannot be null")
        @Size(min = 1, max = 100)
        String name,
        String description
) {}
