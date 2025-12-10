package com.origin.bookstore.repository;

import com.origin.bookstore.model.Book;
import java.util.List;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
