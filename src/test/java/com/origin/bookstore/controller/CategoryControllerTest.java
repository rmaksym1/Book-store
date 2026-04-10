package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import static com.origin.bookstore.util.TestConstants.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private static final Integer CATEGORY_ID = 2;
    private static final Integer INVALID_CATEGORY_ID = 456;
    private static final String CATEGORY_NAME = "Fiction";
    private static final String BOOK_TITLE = "Python Basics";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully create category and return 201")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_Request_ReturnsCreated() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_CATEGORY_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(NAME_JSON_PATH).value(requestDto.name())
                );
    }

    @Test
    @DisplayName("Should return forbidden if user tries to create category")
    @WithMockUser(roles = USER_ROLE)
    void createCategoryByUser_Request_ReturnsForbidden() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_CATEGORY_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @DisplayName("Should return bad request if category doesn't have a name")
    @WithMockUser(roles = ADMIN_ROLE)
    void createCategory_Request_ReturnsBadRequest() throws Exception {
        CreateCategoryRequestDto requestDto =
                new CreateCategoryRequestDto(null, "some description");
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_CATEGORY_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @DisplayName("Should successfully get all categories")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllCategories_Request_ReturnsCategoryDtos() throws Exception {
        mockMvc.perform(get(API_CATEGORY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_CONTENT).isArray())
                .andExpect(jsonPath($_CONTENT + ".length()").value(1)
                );
    }

    @Test
    @DisplayName("Should successfully get a category by id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCategoryById_Request_ReturnsCategoryDto() throws Exception {
        mockMvc.perform(get(API_CATEGORY_PATH_ID, CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andExpect(jsonPath($_ID, is(CATEGORY_ID)))
                .andExpect(jsonPath(NAME_JSON_PATH, is(CATEGORY_NAME))
                );
    }

    @Test
    @DisplayName("Should return 404 if category not found by id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCategoryByInvalidId_Request_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(API_CATEGORY_PATH_ID, INVALID_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound()
                );
    }

    @Test
    @DisplayName("Should successfully delete a category by id")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_Request_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(API_CATEGORY_PATH_ID, CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return forbidden when user is deleting a category")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategoryByUser_Request_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete(API_CATEGORY_PATH_ID, CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should successfully update a category by id")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_Request_ReturnsCategoryDto() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put(API_CATEGORY_PATH_ID, CATEGORY_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_ID, is(CATEGORY_ID)))
                .andExpect(jsonPath(NAME_JSON_PATH, is(requestDto.name()))
                );
    }

    @Test
    @DisplayName("Should return 404 when updating invalid category")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateInvalidCategory_Request_ReturnsNotFound() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put(API_CATEGORY_PATH_ID, INVALID_CATEGORY_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @DisplayName("Should return forbidden when user updating a category")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategoryByUser_Request_ReturnsForbidden() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put(API_CATEGORY_PATH_ID, CATEGORY_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @DisplayName("Should return books by category id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_Request_ReturnsPageOfBookDtos() throws Exception {
        int categoryId = 4;
        mockMvc.perform(get(CATEGORY_ID_BOOKS_API_PATH_ID, categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(BOOK_TITLE)
                );
    }

    @Test
    @DisplayName("Should return empty page by invalid category id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByInvalidCategoryId_Request_ReturnsEmptyPage() throws Exception {
        mockMvc.perform(get(CATEGORY_ID_BOOKS_API_PATH_ID, INVALID_CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty()
                );
    }
}
