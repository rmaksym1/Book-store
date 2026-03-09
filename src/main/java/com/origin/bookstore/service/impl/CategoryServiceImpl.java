package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.BookMapper;
import com.origin.bookstore.mapper.CategoryMapper;
import com.origin.bookstore.model.Category;
import com.origin.bookstore.repository.book.BookRepository;
import com.origin.bookstore.repository.category.CategoryRepository;
import com.origin.bookstore.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final BookMapper bookMapper;

    private final BookRepository bookRepository;

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(
                categoryRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException("Category with id: "
                                + id + " not found!")));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryMapper.toDto(categoryRepository.save(
                categoryMapper.toEntity(createCategoryRequestDto)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto createCategoryRequestDto) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id: " + id
                    + " not found and cannot be updated!");
        }
        Category category = categoryMapper.toEntity(createCategoryRequestDto);
        category.setId(id);

        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id).ifPresent(categoryRepository::delete);
    }

    @Override
    public List<BookDto> getBooksByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
