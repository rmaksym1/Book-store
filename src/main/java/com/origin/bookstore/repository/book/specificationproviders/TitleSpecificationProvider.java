package com.origin.bookstore.repository.book.specificationproviders;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    public static final String KEY = "title";

    @Override
    public String getKey() {
        return KEY;
    }

    public Specification<Book> getSpecification(String title) {
        return (root, query, criteriaBuilder)
                -> root.get(KEY).in(title);
    }
}
