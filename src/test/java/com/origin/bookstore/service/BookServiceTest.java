package com.origin.bookstore.service;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.BookSearchParameters;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.BookMapper;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.Category;
import com.origin.bookstore.repository.book.BookRepository;
import com.origin.bookstore.repository.book.BookSpecificationBuilder;
import com.origin.bookstore.repository.category.CategoryRepository;
import com.origin.bookstore.service.impl.BookServiceImpl;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static com.origin.bookstore.util.TestConstants.pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    public static final Long VALID_BOOK_ID = 1L;
    public static final Long INVALID_ID = 456L;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Should save and return the exact book")
    void save_ValidBook_ReturnsResponseDto() {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        Book book = TestUtil.createBook();
        Category category = TestUtil.createCategory();
        BookDto expectedDto = TestUtil.createBookDto();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(List.of(category));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.save(requestDto);

        assertEquals(expectedDto, actual);
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Throws the exception if categories not found")
    void save_ValidBookWithInvalidCategory_ThrowsException() {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        requestDto.setCategoryIds(Set.of(INVALID_ID));
        Book book = TestUtil.createBook();

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(categoryRepository.findAllById(requestDto.getCategoryIds()))
                .thenReturn(List.of());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.save(requestDto));

        assertEquals("One or more categories not found!", ex.getMessage());
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return a page of response book DTOs")
    void findAll_ValidBooks_ReturnsResponseDtos() {
        Book book = TestUtil.createBook();
        BookDto bookDto = TestUtil.createBookDto();

        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actual = bookService.findAll(pageable);

        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookDto.getTitle(), actual.getContent().get(0).getTitle());

        verify(bookRepository).findAll(pageable);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return a book by id")
    void findById_ReturnsValidBook() {
        Book book = TestUtil.createBook();
        BookDto expectedDto = TestUtil.createBookDto();

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(expectedDto);

        BookDto actual = bookService.findById(VALID_BOOK_ID);

        assertNotNull(actual);
        assertEquals(expectedDto, actual);

        verify(bookRepository).findById(VALID_BOOK_ID);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Should return a book by id")
    void findByWrongId_ThrowsException() {

        when(bookRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.findById(INVALID_ID));

        assertEquals("Can't find book by id: " + INVALID_ID, ex.getMessage());
        verify(bookRepository).findById(INVALID_ID);
    }

    @Test
    @DisplayName("Soft delete a book by id")
    void deleteBookBy_ValidId_ChecksRepository() {
        Book book = TestUtil.createBook();

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));

        bookService.deleteById(VALID_BOOK_ID);

        verify(bookRepository).delete(book);
    }

    @Test
    @DisplayName("Should return an updated book by id")
    void updateBookBy_ValidId_ReturnsBookResponseDto() {
        Book book = TestUtil.createBook();
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        BookDto excepted = TestUtil.createBookDto();

        when(bookRepository.findById(VALID_BOOK_ID)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(excepted);

        BookDto actual = bookService.updateBook(VALID_BOOK_ID, requestDto);

        assertEquals(excepted, actual);
        verify(bookRepository).findById(VALID_BOOK_ID);
        verify(bookMapper).updateBook(requestDto, book);
        verify(bookRepository).save(book);
        verify(bookMapper).toDto(book);
    }

    @Test
    @DisplayName("Throws an exception if book not found")
    void update_InvalidBook_ThrowsException() {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();

        when(bookRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.updateBook(INVALID_ID, requestDto));

        assertEquals("Can't find and update book by id: " + INVALID_ID, ex.getMessage());
        verify(bookRepository).findById(INVALID_ID);
    }

    @Test
    @DisplayName("Should return a page of found books")
    void searchBooks_ReturnsValidBooks() {
        Book book = TestUtil.createBook();

        BookSearchParameters parameters =
                new BookSearchParameters(book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn());

        BookDto bookDto = TestUtil.createBookDto();
        Specification<Book> specification = (root, query, criteriaBuilder) -> null;
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookSpecificationBuilder.build(parameters)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actualPage = bookService.search(parameters, pageable);

        assertNotNull(actualPage);
        assertEquals(1, actualPage.getContent().size());
        assertEquals(bookDto, actualPage.getContent().get(0));

        verify(bookSpecificationBuilder).build(parameters);
        verify(bookRepository).findAll(specification, pageable);
    }
}
