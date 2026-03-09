package com.origin.bookstore.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 100)
    private String name;
    private String description;
}
