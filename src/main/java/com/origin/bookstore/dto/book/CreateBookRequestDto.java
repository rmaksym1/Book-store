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
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Builder
public class CreateBookRequestDto {
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;
    @NotBlank(message = "Author cannot be blank")
    @Size(min = 1, max = 10, message = "Author must be between 1 and 100 characters")
    private String author;
    @NotBlank(message = "ISBN cannot be blank")
    @Pattern(regexp = "\\d{3}-\\d{10}", message = "Invalid ISBN format")
    private String isbn;
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0", inclusive = true, message = "Price must be positive")
    private BigDecimal price;
    @Size(max = 1000, message = "Description is too long")
    private String description;
    @URL(message = "Invalid cover image URL format")
    private String coverImage;
    @NotEmpty(message = "At least one category ID must be provided")
    private Set<Long> categoryIds;
}
