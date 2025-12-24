package com.origin.bookstore.repository.book.specificationproviders;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IsbnSpecificationProvider implements SpecificationProvider<Book> {

    @Override
    public String getKey() {
        return "isbn";
    }

    public Specification<Book> getSpecification(String isbn) {
        return (root, query, criteriaBuilder)
                -> root.get("isbn").in(isbn);
    }
}
