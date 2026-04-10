package com.origin.bookstore.repository;

import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.Category;
import com.origin.bookstore.repository.book.BookRepository;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import java.util.List;
import java.util.Set;

import static com.origin.bookstore.util.TestConstants.ADD_BOOK_PATH;
import static com.origin.bookstore.util.TestConstants.REMOVE_BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Should save book with category and then find it by id")
    void saveThenFind_ReturnsValidBook() {
        Category category = TestUtil.createCategory();

        Book book = TestUtil.createBook();
        book.setCategories(Set.of(category));

        Book savedBook = bookRepository.save(book);
        Book foundBook = bookRepository.findById(savedBook.getId())
                .orElseThrow(() -> new AssertionError("Book not found!"));

        assertEquals(1, foundBook.getCategories().size());
    }

    @Test
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Soft deleting book by id")
    void delete_ShouldMarkAsDeleted() {
        bookRepository.deleteById(1L);

        assertTrue(bookRepository.findById(1L).isEmpty(), "Book should be soft deleted!");
    }

    @Test
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Should find book by category id")
    void findBookByCategoryId_ReturnsValidBook() {
        List<Book> books = bookRepository.findAllByCategoriesId(4L, Pageable.ofSize(1)).toList();

        assertEquals(1, books.size());
    }

    @Test
    @DisplayName("Should throw exception when saving books with same isbn")
    void saveBooksBySameIsbn_ThrowsException() {
        Category category = TestUtil.createCategory();

        Book book = TestUtil.createBook();
        book.setCategories(Set.of(category));

        bookRepository.save(book);

        Book book1 = TestUtil.createBook();

        assertThrows(DataIntegrityViolationException.class, () -> bookRepository.save(book1));
    }
}
