package com.origin.bookstore.repository.book;

import com.origin.bookstore.model.Book;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = "categories")
    Page<Book> findAllByCategoriesId(Long categoryId, Pageable pageable);

    @NonNull
    @EntityGraph(attributePaths = "categories")
    Page<Book> findAll(Pageable pageable);
}
