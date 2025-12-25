package com.origin.bookstore.repository.book.specificationproviders;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "isbn";
    public static final String SPEC_KEY = "isbn";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String isbn) {
        return (root, query, criteriaBuilder)
                -> root.get(SPEC_KEY).in(isbn);
    }
}
