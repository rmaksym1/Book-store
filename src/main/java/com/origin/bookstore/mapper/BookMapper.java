package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.BookDtoWithoutCategoryIds;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.model.Book;
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
}
