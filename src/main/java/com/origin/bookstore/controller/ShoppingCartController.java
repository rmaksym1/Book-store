package com.origin.bookstore.controller;

import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.model.User;
import com.origin.bookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart management", description = "Endpoints for managing carts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book to cart", description = "Add book to shopping cart")
    public ShoppingCartResponseDto addBookToCart(
            @RequestBody @Valid CartItemRequestDto cartItemRequestDto,
            @AuthenticationPrincipal User user) {
        return shoppingCartService.addBookToCart(user,
                cartItemRequestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get items from shopping cart",
            description = "Get item list from shopping cart")
    public ShoppingCartResponseDto getCartItems(
            @AuthenticationPrincipal User user) {
        return shoppingCartService.getShoppingCartByUserId(user);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update the books quantity in shopping cart",
            description = "Update the books quantity in shopping cart")
    public ShoppingCartResponseDto updateBookQuantityInCart(
            @PathVariable Long cartItemId,
            @RequestBody @Valid UpdateCartItemRequestDto updateCartItemRequestDto,
            @AuthenticationPrincipal User user) {
        return shoppingCartService.updateBookQuantityInCart(user,
                cartItemId, updateCartItemRequestDto);
    }

    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a book from shopping cart",
            description = "Delete a book from shopping cart")
    public void deleteBookFromCart(@PathVariable Long cartItemId,
                                   @AuthenticationPrincipal User user) {
        shoppingCartService.deleteBookFromCart(user, cartItemId);
    }
}
