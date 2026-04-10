package com.origin.bookstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
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
import java.util.Set;

import static com.origin.bookstore.util.TestConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {
    private static final String TITLE_JSON_PATH =
            "$.title";
    private static final String AUTHOR_JSON_PATH =
            "$.author";
    private static final Integer BOOK_ID = 7;
    private static final Integer INVALID_BOOK_ID = 999;
    private static final String BOOK_AUTHOR = "Sam Sapiol";
    private static final String BOOK_TITLE = "Python Basics";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should successfully create book")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_Request_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        requestDto.setCategoryIds(Set.of(4L));
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_BOOKS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TITLE_JSON_PATH).value(requestDto.getTitle()))
                .andExpect(jsonPath(AUTHOR_JSON_PATH).value(requestDto.getAuthor()));
    }

    @Test
    @DisplayName("Should return bad request if book doesn't have a title")
    @WithMockUser(roles = ADMIN_ROLE)
    void createBookWithoutTitle_Request_ReturnsBadRequest() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        requestDto.setTitle(null);
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_BOOKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()
                );
    }

    @Test
    @DisplayName("Should return forbidden if user tries to create a book")
    @WithMockUser(roles = USER_ROLE)
    void createBookByUser_Request_ReturnsForbidden() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        requestDto.setCategoryIds(Set.of(5L));
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(API_BOOKS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @DisplayName("Should successfully get all books")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllBooks_Request_ReturnsBookDtos() throws Exception {
        mockMvc.perform(get(API_BOOKS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_CONTENT).isArray())
                .andExpect(jsonPath($_CONTENT + ".length()").value(1));
    }

    @Test
    @DisplayName("Should successfully get a book by id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findBookById_Request_ReturnsBookDto() throws Exception {
        mockMvc.perform(get(API_BOOKS_PATH_ID, BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                .andExpect(jsonPath($_ID, is(BOOK_ID)))
                .andExpect(jsonPath(TITLE_JSON_PATH, is(BOOK_TITLE)))
                .andExpect(jsonPath(AUTHOR_JSON_PATH, is(BOOK_AUTHOR)));
    }

    @Test
    @DisplayName("Should return 404 if book not found by id")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findBookByInvalidId_Request_ReturnsNotFound() throws Exception {
        mockMvc.perform(get(API_BOOKS_PATH_ID, INVALID_BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound()
                );
    }

    @Test
    @DisplayName("Should successfully delete a book by id")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_Request_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete(API_BOOKS_PATH_ID, BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return forbidden when user is deleting a book")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBookByUser_Request_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete(API_BOOKS_PATH_ID, BOOK_ID)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should successfully update a book by id")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBook_Request_ReturnsBookDto() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

                mockMvc.perform(put(API_BOOKS_PATH_ID, BOOK_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath($_ID, is(BOOK_ID)))
                        .andExpect(jsonPath(TITLE_JSON_PATH, is(requestDto.getTitle())))
                        .andExpect(jsonPath(AUTHOR_JSON_PATH, is(requestDto.getAuthor()))
                        );
    }

    @Test
    @DisplayName("Should return 404 when updating invalid book")
    @WithMockUser(roles = ADMIN_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateInvalidBook_Request_ReturnsNotFound() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put(API_BOOKS_PATH_ID, INVALID_BOOK_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    @DisplayName("Should return forbidden when user updating a book")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookByUser_Request_ReturnsForbidden() throws Exception {
        CreateBookRequestDto requestDto = TestUtil.createBookRequestDto();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put(API_BOOKS_PATH_ID, BOOK_ID)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()
                );
    }

    @Test
    @DisplayName("Should return books matching search parameters")
    @WithMockUser(roles = USER_ROLE)
    @Sql(scripts = ADD_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_BOOK_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void searchBooks_ValidParameters_ReturnsMatchingBooks() throws Exception {

        mockMvc.perform(get(API_BOOKS_SEARCH_PATH)
                        .param("authors", BOOK_AUTHOR)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath($_CONTENT, hasSize(1)))
                .andExpect(jsonPath($_CONTENT + "[0].title", is(BOOK_TITLE)))
                .andExpect(jsonPath( $_CONTENT + "[0].author", is(BOOK_AUTHOR))
                );
    }
}
