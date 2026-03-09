package com.origin.bookstore.service;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto);

    CategoryDto update(Long id, CreateCategoryRequestDto createCategoryRequestDto);

    void deleteById(Long id);

    List<BookDto> getBooksByCategoryId(Long id, Pageable pageable);
}
