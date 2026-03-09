package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto bookRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateBook(CreateBookRequestDto bookDto, @MappingTarget Book book);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @AfterMapping
    default void setCategoryIds(@MappingTarget BookDto bookDto, Book book) {
        if (book.getCategories() != null) {
            bookDto.setCategoryIds(book.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toSet()));
        }
    }

    @AfterMapping
    default void setCategories(@MappingTarget Book book, CreateBookRequestDto bookRequestDto) {
        if (bookRequestDto.getCategoryIds() != null) {
            Set<Category> categories = bookRequestDto.getCategoryIds().stream()
                    .map(id -> {
                        Category category = new Category();
                        category.setId(id);
                        return category;
                    })
                    .collect(Collectors.toSet());
            book.setCategories(categories);
        }
    }
}
