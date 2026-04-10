package com.origin.bookstore.service;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.BookMapper;
import com.origin.bookstore.mapper.CategoryMapper;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.Category;
import com.origin.bookstore.repository.book.BookRepository;
import com.origin.bookstore.repository.category.CategoryRepository;
import com.origin.bookstore.service.impl.CategoryServiceImpl;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;
import java.util.Optional;
import static com.origin.bookstore.util.TestConstants.pageable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    public static final Long VALID_CATEGORY_ID = 1L;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Should return all categories")
    void findAll_ReturnsCategoriesResponsePage() {
        Category category = TestUtil.createCategory();
        CategoryDto categoryDto = TestUtil.createCategoryDto();
        Page<Category> categoryPage = new PageImpl<>(List.of(category), pageable, 1);

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> actual = categoryService.findAll(pageable);

        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(categoryDto.name(), actual.getContent().get(0).name());

        verify(categoryRepository).findAll(pageable);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should return category by id")
    void find_CategoryById_ReturnsCategoryResponse() {
        Category category = TestUtil.createCategory();
        CategoryDto categoryDto = TestUtil.createCategoryDto();

        when(categoryMapper.toDto(category))
                .thenReturn(categoryDto);
        when(categoryRepository.findById(VALID_CATEGORY_ID))
                .thenReturn(Optional.of(category));

        categoryService.getById(VALID_CATEGORY_ID);

        verify(categoryMapper).toDto(category);
        verify(categoryRepository).findById(VALID_CATEGORY_ID);
    }

    @Test
    @DisplayName("Throws an exception if category not found")
    void find_CategoryByInvalidId_ThrowsException() {

        when(categoryRepository.findById(VALID_CATEGORY_ID))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(VALID_CATEGORY_ID));

        assertEquals("Category with id: "
                + VALID_CATEGORY_ID + " not found!", ex.getMessage());

        verify(categoryRepository).findById(VALID_CATEGORY_ID);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("Should save a category and return response dto")
    void save_ValidCategory_ReturnsResponseDto() {
        Category category = TestUtil.createCategory();
        CategoryDto expected = TestUtil.createCategoryDto();
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toEntity(requestDto)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Should update a category")
    void update_ValidCategory_ReturnsResponseDto() {
        Category category = TestUtil.createCategory();
        CategoryDto expected = TestUtil.createCategoryDto();
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.update(VALID_CATEGORY_ID, requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository).findById(VALID_CATEGORY_ID);
        verify(categoryRepository).save(category);
        verify(categoryMapper).toDto(category);
    }

    @Test
    @DisplayName("Throws an exception if category not found")
    void update_InvalidCategory_ThrowsException() {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(VALID_CATEGORY_ID, requestDto));

        assertEquals("Category with id: " + VALID_CATEGORY_ID + " not found!",
                ex.getMessage());
        verify(categoryRepository).findById(VALID_CATEGORY_ID);
    }

    @Test
    @DisplayName("Soft delete a category by id")
    void deleteCategoryBy_ValidId_ChecksRepository() {
        Category category = TestUtil.createCategory();

        when(categoryRepository.findById(VALID_CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.deleteById(VALID_CATEGORY_ID);

        verify(categoryRepository).delete(category);
    }

    @Test
    @DisplayName("Should return a page of books by category id")
    void getBooksBy_CategoryId_ReturnsBookResponseDto() {
        Book book = TestUtil.createBook();
        BookDto bookDto = TestUtil.createBookDto();
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookRepository.findAllByCategoriesId(VALID_CATEGORY_ID, pageable)).thenReturn(bookPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actual = categoryService.getBooksByCategoryId(VALID_CATEGORY_ID, pageable);

        assertNotNull(actual);
        assertEquals(1, actual.getContent().size());
        assertEquals(bookDto, actual.getContent().get(0));

        verify(bookRepository).findAllByCategoriesId(VALID_CATEGORY_ID, pageable);
        verify(bookMapper).toDto(book);
    }
}
