package com.origin.bookstore.repository;

import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.origin.bookstore.repository.user.UserRepository;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import static com.origin.bookstore.util.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save shopping cart and test if it's id is equal to user id")
    void saveShoppingCartAndCheckEquality_ReturnsSameIds() {
        User user = userRepository.save(TestUtil.createUser());

        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setUser(user);

        ShoppingCart shoppingCart1 = shoppingCartRepository.save(shoppingCart);

        assertEquals(shoppingCart1.getId(), user.getId());
    }

    @Test
    @DisplayName("Soft deleting shopping cart by id")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void softDeleteShoppingCartById_ShouldMarkAsDeleted() {
        shoppingCartRepository.deleteById(1L);

        assertTrue(shoppingCartRepository.findById(1L).isEmpty(),
                "Shopping cart should be soft deleted!");
    }

    @Test
    @DisplayName("Should return the shopping cart by user")
    @Sql(scripts = ADD_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = REMOVE_SHOPPINGCART_PATH,
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findShoppingCartByUser_ReturnsShoppingCart() {
        User user = TestUtil.createUser();
        user.setId(5L);

        assertTrue(shoppingCartRepository.findByUser(user).isPresent(),
                "Shopping cart is not found by user id");
    }
}
