package com.origin.bookstore.repository;

import com.origin.bookstore.model.Category;
import com.origin.bookstore.repository.category.CategoryRepository;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import static com.origin.bookstore.util.TestConstants.ADD_CATEGORY_PATH;
import static com.origin.bookstore.util.TestConstants.REMOVE_CATEGORY_PATH;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("Save then find a category by id")
    void saveAndFind_ValidCategory_ReturnsCategory() {
        Category category = TestUtil.createCategory();

        Category savedCategory = categoryRepository.save(category);
        Category category1 = categoryRepository.findById(savedCategory.getId())
                .orElseThrow(() -> new AssertionError("Category not found!"));

        assertEquals(category.getName(), category1.getName());
    }

    @Test
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Soft deleting category by id")
    void delete_ShouldMarkAsDeleted() {
        Long id = 1L;
        categoryRepository.deleteById(id);

        assertTrue(categoryRepository.findById(id).isEmpty(), "Category should be soft-deleted!");
    }

    @Test
    @DisplayName("Should throw exception when saving categories with same name")
    void saveCategoriesBySameName_ThrowsException() {
        Category category = TestUtil.createCategory();

        Category duplicatecategory = TestUtil.createCategory();

        categoryRepository.save(category);

        assertThrows(DataIntegrityViolationException.class, () -> categoryRepository.save(duplicatecategory));
    }
}
