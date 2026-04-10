package com.origin.bookstore.repository;

import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import static com.origin.bookstore.util.TestConstants.ADD_USER_PATH;
import static com.origin.bookstore.util.TestConstants.REMOVE_USERS_PATH;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should successfully save and find user by id")
    void saveUser_ThenFindUserById_ReturnsUser() {
        User user = TestUtil.createUser();
        User savedUser = userRepository.save(user);

        assertTrue(userRepository.findById(savedUser.getId()).isPresent(), "User is not found in DB!");
    }

    @Test
    @DisplayName("Should throw an exception if email is occupied")
    @Sql(scripts = ADD_USER_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_USERS_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void saveUser_ThrowsException_WhenEmailIsNotUnique() {
        User user = TestUtil.createUser();
        user.setEmail("rudycooper@gmail.com");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(user));
    }

    @Test
    @DisplayName("Soft deleting user by id")
    @Sql(scripts = ADD_USER_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_USERS_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void softDeleteUser_ShouldMarkAsDeleted() {
        userRepository.deleteById(5L);

        assertTrue(userRepository.findById(5L).isEmpty(), "User should be soft deleted!");
    }
}
