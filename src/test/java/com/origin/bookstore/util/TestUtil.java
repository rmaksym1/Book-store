package com.origin.bookstore.util;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.Category;
import java.math.BigDecimal;
import java.util.Set;

public class TestUtil {
    public static Book createBook() {
        return Book.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .build();
    }

    public static Category createCategory() {
        return Category.builder()
                .name("Chemistry")
                .description("Books for chemistry")
                .build();
    }

    public static CreateBookRequestDto createBookRequestDto() {
        return CreateBookRequestDto.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .categoryIds(Set.of(1L))
                .build();
    }

    public static BookDto createBookDto() {
        return BookDto.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .build();
    }

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto(
                "Chemistry",
                "Books for chemistry"
        );
    }

    public static CategoryDto createCategoryDto() {
        return new CategoryDto(
                1L,
                "Chemistry",
                "Books for chemistry"
        );
    }
}
