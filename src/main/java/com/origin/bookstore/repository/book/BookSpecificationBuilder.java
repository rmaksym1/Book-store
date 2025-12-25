package com.origin.bookstore.repository.book;

import com.origin.bookstore.dto.BookSearchParameters;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.SpecificationBuilder;
import com.origin.bookstore.repository.book.specificationproviders.AuthorSpecificationProvider;
import com.origin.bookstore.repository.book.specificationproviders.IsbnSpecificationProvider;
import com.origin.bookstore.repository.book.specificationproviders.TitleSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {

    private final BookSpecificationProviderManager bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.title() != null && !searchParameters.title().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(TitleSpecificationProvider.KEY)
                    .getSpecification(searchParameters.title()));
        }
        if (searchParameters.author() != null && !searchParameters.author().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(AuthorSpecificationProvider.KEY)
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.isbn() != null && !searchParameters.isbn().isEmpty()) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(IsbnSpecificationProvider.KEY)
                    .getSpecification(searchParameters.isbn()));
        }
        return spec;
    }
}
