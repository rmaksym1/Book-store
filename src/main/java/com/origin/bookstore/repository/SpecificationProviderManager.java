package com.origin.bookstore.repository;

import com.origin.bookstore.model.Book;

public interface SpecificationProviderManager {
    public SpecificationProvider<Book> getSpecificationProvider(String key);
}
