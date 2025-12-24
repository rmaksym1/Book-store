package com.origin.bookstore.service;

import com.origin.bookstore.dto.BookDto;
import com.origin.bookstore.dto.CreateBookRequestDto;
import java.util.List;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    List<BookDto> findAll();

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto updateBook(Long id, BookDto bookDto);
}
