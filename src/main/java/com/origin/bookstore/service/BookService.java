package com.origin.bookstore.service;

import com.origin.bookstore.dto.BookDto;
import com.origin.bookstore.dto.BookSearchParameters;
import com.origin.bookstore.dto.CreateBookRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto bookRequestDto);

    Page<BookDto> findAll(Pageable pageable);

    BookDto findById(Long id);

    void deleteById(Long id);

    BookDto updateBook(Long id, BookDto bookDto);

    Page<BookDto> search(BookSearchParameters bookSearchParameters, Pageable pageable);
}
