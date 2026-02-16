package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.BookSearchParameters;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.BookMapper;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.book.BookRepository;
import com.origin.bookstore.repository.book.BookSpecificationBuilder;
import com.origin.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto bookRequestDto) {
        Book book = bookMapper.toModel(bookRequestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Page<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(bookMapper::toDto);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find book by id: " + id)
        );
        return bookMapper.toDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.findById(id).ifPresent(bookRepository::delete);
    }

    @Override
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find and update book by id: " + id)
        );

        bookMapper.updateBook(bookDto, book);
        bookRepository.save(book);

        return bookMapper.toDto(book);
    }

    @Override
    public Page<BookDto> search(BookSearchParameters bookSearchParameters, Pageable pageable) {
        Specification<Book> bookSpecification =
                bookSpecificationBuilder.build(bookSearchParameters);

        return bookRepository.findAll(bookSpecification, pageable)
                .map(bookMapper::toDto);
    }
}
