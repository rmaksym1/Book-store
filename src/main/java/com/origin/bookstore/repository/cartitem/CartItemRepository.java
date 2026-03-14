package com.origin.bookstore.repository.cartitem;

import com.origin.bookstore.model.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndShoppingCartUserId(Long cartItemId, Long userId);
}
