package com.origin.bookstore.dto.book;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateBookRequestDto {
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;
    @NotBlank
    @Size(min = 1, max = 100)
    private String author;
    @NotBlank
    @Pattern(regexp = "\\d{3}-\\d{10}")
    private String isbn;
    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    private BigDecimal price;
    private String description;
    private String coverImage;
    @NotEmpty
    private Set<Long> categoryIds;
}
