package com.origin.bookstore.mapper;

import com.origin.bookstore.config.MapperConfig;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto createCategoryRequestDto);
}
