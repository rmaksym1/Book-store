package com.origin.bookstore.controller;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category Management", description = "Endpoints for managing categories")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Create a category", description = "Create a category")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(
            @RequestBody @Valid
            CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryService.save(createCategoryRequestDto);
    }

    @Operation(summary = "Get all categories", description = "Get all categories")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(summary = "Get category by id", description = "Get category by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update category by id", description = "Update category by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid
                                      CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryService.update(id, createCategoryRequestDto);
    }

    @Operation(summary = "Delete category by id", description = "Delete category by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Get books by category id", description = "Get books by category id")
    @GetMapping("/{id}/books")
    @PreAuthorize("hasRole('USER')")
    public Page<BookDto> getBooksByCategoryId(@PathVariable Long id, Pageable pageable) {
        return categoryService.getBooksByCategoryId(id, pageable);
    }
}
