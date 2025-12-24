package com.origin.bookstore.repository.book;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.repository.SpecificationProvider;
import com.origin.bookstore.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager {

    private final List<SpecificationProvider<Book>> specificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(()
                        -> new RuntimeException("Can't find correct "
                                + "specification provider for key " + key));
    }
}
