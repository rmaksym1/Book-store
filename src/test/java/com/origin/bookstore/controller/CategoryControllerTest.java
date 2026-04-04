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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {
    private static final String API_PATH =
            "/categories";
    private static final String API_PATH_ID =
            "/categories/{id}";
    private static final String ADD_CATEGORY_PATH =
            "/database/categories/add-category-to-categories-table.sql";
    private static final String REMOVE_CATEGORY_PATH =
            "/database/categories/remove-category-from-categories-table.sql";
    private static final String ID_JSON_PATH =
            "$.id";
    private static final String NAME_JSON_PATH =
            "$.name";
    private static final String CONTENT_JSON_PATH =
            "$.content";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";
    private static final Integer CATEGORY_ID = 2;
    private static final Integer INVALID_CATEGORY_ID = 456;
    private static final String CATEGORY_NAME = "Fiction";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully create category and return 201")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_CATEGORY_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_Request_ReturnsCreated() throws Exception {
        CreateCategoryRequestDto requestDto = TestUtil.createCategoryRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_PATH)
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

        mockMvc.perform(post(API_PATH)
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

        mockMvc.perform(post(API_PATH)
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
        mockMvc.perform(get(API_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CONTENT_JSON_PATH).isArray())
                .andExpect(jsonPath(CONTENT_JSON_PATH + ".length()").value(1)
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
        mockMvc.perform(get(API_PATH_ID, CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andExpect(jsonPath(ID_JSON_PATH, is(CATEGORY_ID)))
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
        mockMvc.perform(get(API_PATH_ID, INVALID_CATEGORY_ID)
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
        mockMvc.perform(delete(API_PATH_ID, CATEGORY_ID)
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
        mockMvc.perform(delete(API_PATH_ID, CATEGORY_ID)
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

        mockMvc.perform(put(API_PATH_ID, CATEGORY_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_JSON_PATH, is(CATEGORY_ID)))
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

        mockMvc.perform(put(API_PATH_ID, INVALID_CATEGORY_ID)
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

        mockMvc.perform(put(API_PATH_ID, CATEGORY_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()
                );
    }
}
