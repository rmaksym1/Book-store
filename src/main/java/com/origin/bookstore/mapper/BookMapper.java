package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.BookDto;
import com.origin.bookstore.dto.CreateBookRequestDto;
import com.origin.bookstore.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    public BookDto toDto(Book book);

    public Book toModel(CreateBookRequestDto bookRequestDto);
}
