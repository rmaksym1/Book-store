package com.origin.bookstore.service.impl;

import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.CartItemMapper;
import com.origin.bookstore.mapper.ShoppingCartMapper;
import com.origin.bookstore.model.CartItem;
import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.cartitem.CartItemRepository;
import com.origin.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.origin.bookstore.service.ShoppingCartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartResponseDto addBookToCart(User user, CartItemRequestDto cartItemRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUser(user).orElseThrow(
                        () -> new EntityNotFoundException("Shopping cart by user id: "
                                + user.getId() + " not found")
                );

        CartItem cartItem = shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook()
                        .getId()
                        .equals(cartItemRequestDto.bookId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem cartItem1 = cartItemMapper.toEntity(cartItemRequestDto);
                    cartItem1.setShoppingCart(shoppingCart);
                    shoppingCart.getCartItems().add(cartItem1);
                    return cartItem1;
                });

        cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.quantity());
        return shoppingCartMapper.toDto(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ShoppingCartResponseDto getShoppingCartByUserId(User user) {
        return shoppingCartMapper.toDto(shoppingCartRepository
                .findByUser(user).orElseThrow(
                        () -> new EntityNotFoundException("Shopping cart by user id: "
                                + user.getId() + " not found")
                ));
    }

    @Override
    public void createShoppingCartForUser(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto updateBookQuantityInCart(
            User user,
            Long cartItemId,
            UpdateCartItemRequestDto updateCartItemRequestDto) {
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartUserId(
                        cartItemId, user.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cart item by id: "
                                + cartItemId)
                );

        cartItem.setQuantity(updateCartItemRequestDto.quantity());

        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find shopping cart by user id: "
                                + user.getId())
                );

        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void deleteBookFromCart(User user, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartUserId(
                cartItemId,
                user.getId()).orElseThrow(
                    () -> new EntityNotFoundException("Can't find cart item by id: "
                        + cartItemId)
        );

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearShoppingCart(User user) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user).orElseThrow(
                () -> new EntityNotFoundException("Can't find shopping cart by user id: "
                        + user.getId())
        );

        shoppingCart.getCartItems().clear();
    }
}
