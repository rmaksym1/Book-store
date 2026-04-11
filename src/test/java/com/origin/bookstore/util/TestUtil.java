package com.origin.bookstore.util;

import com.origin.bookstore.dto.book.BookDto;
import com.origin.bookstore.dto.book.CreateBookRequestDto;
import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.CartItemResponseDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.category.CategoryDto;
import com.origin.bookstore.dto.category.CreateCategoryRequestDto;
import com.origin.bookstore.dto.order.OrderRequestDto;
import com.origin.bookstore.dto.order.OrderResponseDto;
import com.origin.bookstore.dto.order.UpdateOrderStatusRequestDto;
import com.origin.bookstore.dto.orderitem.OrderItemResponseDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.dto.user.UserRegistrationRequestDto;
import com.origin.bookstore.dto.user.UserResponseDto;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.CartItem;
import com.origin.bookstore.model.Category;
import com.origin.bookstore.model.Order;
import com.origin.bookstore.model.OrderItem;
import com.origin.bookstore.model.Role;
import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class TestUtil {
    public static Book createBook() {
        return Book.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .build();
    }

    public static Category createCategory() {
        return Category.builder()
                .name("Chemistry")
                .description("Books for chemistry")
                .build();
    }

    public static CreateBookRequestDto createBookRequestDto() {
        return CreateBookRequestDto.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .categoryIds(Set.of(1L))
                .build();
    }

    public static BookDto createBookDto() {
        return BookDto.builder()
                .title("Chemistry for Beginners")
                .author("W. W.")
                .isbn("978-0195183429")
                .price(BigDecimal.valueOf(24.99))
                .build();
    }

    public static CreateCategoryRequestDto createCategoryRequestDto() {
        return new CreateCategoryRequestDto(
                "Chemistry",
                "Books for chemistry"
        );
    }

    public static CategoryDto createCategoryDto() {
        return new CategoryDto(
                1L,
                "Chemistry",
                "Books for chemistry"
        );
    }

    public static User createUser() {
        return User.builder()
                .email("arthur_morgan@gmail.com")
                .password("fe054fdb41fe50de342640ecda4c8d" +
                        "d111c17db843b0c2afad053dcfcc4b5a4f")
                .firstName("Arthur")
                .lastName("Morgan")
                .build();
    }

    public static CartItem createCartItem() {
        return CartItem.builder()
                .build();
    }

    public static ShoppingCart createShoppingCart() {
        return ShoppingCart.builder()
                .build();
    }

    public static ShoppingCartResponseDto createShoppingCartResponseDto() {
        return new ShoppingCartResponseDto();
    }

    public static CartItemResponseDto createCartItemResponseDto() {
        return new CartItemResponseDto(
                1L,
                1L,
                "BookName",
                10
        );
    }

    public static CartItemRequestDto createCartItemRequestDto() {
        return new CartItemRequestDto(
                1L,
                10
        );
    }

    public static UpdateCartItemRequestDto createUpdateCartItemRequestDto() {
        return new UpdateCartItemRequestDto(10);
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return UserRegistrationRequestDto.builder()
                .email("arthur_morgan@gmail.com")
                .password("somePassword")
                .repeatPassword("somePassword")
                .firstName("Arthur")
                .lastName("Morgan")
                .build();
    }

    public static UserResponseDto createUserResponseDto() {
        return UserResponseDto.builder()
                .email("arthur_morgan@gmail.com")
                .password("baae90bd064867ab28f034d6ed40ef14684012e4c0567181eaee3494a2358695")
                .firstName("Arthur")
                .lastName("Morgan")
                .build();
    }

    public static Order createOrder() {
        return Order.builder()
                .id(5L)
                .orderDateTime(LocalDateTime.now())
                .total(BigDecimal.valueOf(66))
                .shippingAddress("3828 Piermont Dr NE, Albuquerque, New Mexico")
                .build();
    }

    public static OrderRequestDto createOrderRequestDto() {
        return new OrderRequestDto("3828 Piermont Dr NE, Albuquerque, New Mexico");
    }

    public static OrderResponseDto createOrderResponseDto() {
        return OrderResponseDto.builder()
                .orderDateTime(LocalDateTime.now())
                .build();
    }

    public static OrderItem createOrderItem() {
        return OrderItem.builder()
                .price(BigDecimal.valueOf(10))
                .build();
    }

    public static OrderItemResponseDto createOrderItemResponseDto() {
        return new OrderItemResponseDto(1L, 1L, 5);
    }

    public static UpdateOrderStatusRequestDto createUpdateOrderStatusRequestDto() {
        return new UpdateOrderStatusRequestDto("DELIVERED");
    }
}
