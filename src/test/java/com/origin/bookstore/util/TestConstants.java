package com.origin.bookstore.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class TestConstants {
    public static final String ADD_USER_PATH =
            "/database/users/add-user-to-users-table.sql";
    public static final String ADD_ADMIN_PATH =
            "/database/users/add-admin-to-users-table.sql";
    public static final String ADD_BOOK_PATH =
            "/database/books/add-books-with-categories.sql";
    public static final String REMOVE_BOOK_PATH =
            "/database/books/remove-books-with-categories.sql";
    public static final String ADD_CATEGORY_PATH =
            "/database/categories/add-category-to-categories-table.sql";
    public static final String REMOVE_CATEGORY_PATH =
            "/database/categories/remove-category-from-categories-table.sql";
    public static final String REMOVE_USERS_PATH =
            "/database/users/remove-users-from-users-table.sql";
    public static final String ADD_SHOPPINGCART_PATH =
            "/database/shoppingcarts/add-shopping-cart-with-user-to-tables.sql";
    public static final String REMOVE_SHOPPINGCART_PATH =
            "/database/shoppingcarts/remove-shopping-cart-with-user-from-tables.sql";
    public static final String ADD_ORDER_PATH =
            "/database/orders/add-order-to-orders-table.sql";
    public static final String REMOVE_ORDER_PATH =
            "/database/orders/remove-order-from-orders-table.sql";
    public static final String CLEANUP_DB_PATH =
            "/database/cleanup-db.sql";

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    public static final String $_TOTAL_ELEMENTS = "$.totalElements";
    public static final String $_CONTENT = "$.content";
    public static final String $_ID = "$.id";
    public static final String $_QUANTITY = "$.quantity";
    public static final String $_STATUS = "$.status";
    public static final String $_TOTAL = "$.total";

    public static final String API_CATEGORY_PATH =
            "/categories";
    public static final String API_CATEGORY_PATH_ID =
            "/categories/{id}";
    public static final String CATEGORY_ID_BOOKS_API_PATH_ID =
            "/categories/{id}/books";
    public static final String NAME_JSON_PATH =
            "$.name";
    public static final String REGISTRATION_PATH =
            "/auth/registration";
    public static final String LOGIN_PATH =
            "/auth/login";
    public static final String API_BOOKS_PATH =
            "/books";
    public static final String API_BOOKS_PATH_ID =
            "/books/{id}";
    public static final String API_BOOKS_SEARCH_PATH =
            "/books/search";
    public static final String ORDER_ID_ITEMS_ID_URL =
            "/orders/{orderId}/items/{id}";
    public static final String ORDERS_ORDER_ID_ITEMS_URL =
            "/orders/{orderId}/items";
    public static final String ORDERS_ID_URL =
            "/orders/{id}";
    public static final String ORDERS_URL =
            "/orders";
    public static final String CART_URL =
            "/cart";
    public static final String CART_ITEMS_CART_ITEM_ID_URL =
            "/cart/items/{cartItemId}";

    public static final Pageable pageable = PageRequest.of(0, 10);
}
