package com.origin.bookstore.repository.orderitem;

import com.origin.bookstore.model.OrderItem;
import com.origin.bookstore.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<OrderItem> findByIdAndOrderIdAndOrderUser(Long orderItemId, Long orderId, User user);

    List<OrderItem> findAllByOrderIdAndOrderUser(Long orderId, User user);
}
