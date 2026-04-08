package com.origin.bookstore.service;

import com.origin.bookstore.dto.cartitem.CartItemRequestDto;
import com.origin.bookstore.dto.cartitem.UpdateCartItemRequestDto;
import com.origin.bookstore.dto.shoppingcart.ShoppingCartResponseDto;
import com.origin.bookstore.exception.EntityNotFoundException;
import com.origin.bookstore.mapper.CartItemMapper;
import com.origin.bookstore.mapper.ShoppingCartMapper;
import com.origin.bookstore.model.Book;
import com.origin.bookstore.model.CartItem;
import com.origin.bookstore.model.ShoppingCart;
import com.origin.bookstore.model.User;
import com.origin.bookstore.repository.cartitem.CartItemRepository;
import com.origin.bookstore.repository.shoppingcart.ShoppingCartRepository;
import com.origin.bookstore.service.impl.ShoppingCartServiceImpl;
import com.origin.bookstore.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {

    @Mock
    ShoppingCartRepository shoppingCartRepository;

    @Mock
    CartItemMapper cartItemMapper;

    @Mock
    ShoppingCartMapper shoppingCartMapper;

    @Mock
    CartItemRepository cartItemRepository;

    @InjectMocks
    ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Should add a cartItem to cart successfully")
    void addCartItemToCart_ReturnsShoppingCartResponseDto() {
        ShoppingCartResponseDto shoppingCartResponseDto = TestUtil.createShoppingCartResponseDto();
        CartItem cartItem = TestUtil.createCartItem();
        CartItemRequestDto cartItemRequestDto = TestUtil.createCartItemRequestDto();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setCartItems(new HashSet<>());
        User user = TestUtil.createUser();

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(cartItemMapper.toEntity(cartItemRequestDto)).thenReturn(cartItem);
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartResponseDto);

        ShoppingCartResponseDto shoppingCartResponseDto1 = shoppingCartService.addBookToCart(user, cartItemRequestDto);

        assertEquals(shoppingCartResponseDto, shoppingCartResponseDto1);
        verify(shoppingCartRepository).findByUser(user);
        verify(cartItemMapper).toEntity(cartItemRequestDto);
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Should throw and exception if shopping cart not found")
    void addCartItemToInvalidCart_ThrowsException() {
        CartItemRequestDto cartItemRequestDto = TestUtil.createCartItemRequestDto();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setCartItems(new HashSet<>());
        User user = TestUtil.createUser();

        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addBookToCart(user, cartItemRequestDto)
        );

        assertEquals("Shopping cart by user id: "
                + user.getId() + " not found", ex.getMessage());
        verify(shoppingCartRepository).findByUser(user);
    }

    @Test
    @DisplayName("Quantity should be updated if item is in cart")
    void addExistingItemToCart_UpdatesQuantity() {
        User user = TestUtil.createUser();
        Book book = TestUtil.createBook();
        book.setId(7L);

        CartItem existingItem = new CartItem();
        existingItem.setBook(book);
        existingItem.setQuantity(4);

        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setCartItems(Set.of(existingItem));

        CartItemRequestDto cartItemRequestDto =
                new CartItemRequestDto(book.getId(), 3);

        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartRepository.save(shoppingCart))
                .thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(TestUtil.createShoppingCartResponseDto());

        shoppingCartService.addBookToCart(user, cartItemRequestDto);

        assertEquals(7, existingItem.getQuantity(),
                "Quantity must've been updated without adding new cartItem!");
        verify(cartItemMapper, never()).toEntity(any());
    }

    @Test
    @DisplayName("Should return a shopping cart by user")
    void getShoppingCartByUser_ReturnsShoppingCart() {
        User user = TestUtil.createUser();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        ShoppingCartResponseDto responseDto = TestUtil.createShoppingCartResponseDto();

        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(responseDto);

        shoppingCartService.getShoppingCartByUserId(user);

        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartMapper).toDto(shoppingCart);
    }

    @Test
    @DisplayName("Should throw an exception if shopping cart not found")
    void getShoppingCartByInvalidUser_ThrowsException() {
        User user = TestUtil.createUser();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();

        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getShoppingCartByUserId(user));

        assertEquals("Shopping cart by user id: "
                + user.getId() + " not found", ex.getMessage());
        verify(shoppingCartRepository).findByUser(user);
    }

    @Test
    @DisplayName("Should create a new shopping cart for user")
    void createShoppingCartForUser_CreatesNewShoppingCart() {
        User user = TestUtil.createUser();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setUser(user);

        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenReturn(shoppingCart);

        shoppingCartService.createShoppingCartForUser(user);
        verify(shoppingCartRepository).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("Should update book quantity in cart successfully")
    void updateBookQuantityInCart_ValidRequest_ReturnsResponseDto() {
        User user = TestUtil.createUser();
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = TestUtil.createUpdateCartItemRequestDto();

        CartItem cartItem = TestUtil.createCartItem();
        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        ShoppingCartResponseDto expectedDto = TestUtil.createShoppingCartResponseDto();

        when(cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, user.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart))
                .thenReturn(expectedDto);

        ShoppingCartResponseDto actualDto =
                shoppingCartService.updateBookQuantityInCart(user, cartItemId, requestDto);

        assertEquals(expectedDto, actualDto);
        assertEquals(10, cartItem.getQuantity());

        verify(cartItemRepository).findByIdAndShoppingCartUserId(cartItemId, user.getId());
        verify(shoppingCartRepository).findByUser(user);
    }

    @Test
    @DisplayName("Should throw exception when cart item is not found")
    void updateBookQuantityInCart_CartItemNotFound_ThrowsException() {
        User user = TestUtil.createUser();
        Long cartItemId = 99L;
        UpdateCartItemRequestDto requestDto = TestUtil.createUpdateCartItemRequestDto();

        when(cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, user.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.updateBookQuantityInCart(user, cartItemId, requestDto));

        assertEquals("Can't find cart item by id: 99", ex.getMessage());
        verify(cartItemRepository).findByIdAndShoppingCartUserId(cartItemId, user.getId());
        verifyNoMoreInteractions(shoppingCartRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Should throw exception when shopping cart is not found")
    void updateBookQuantityInCart_ShoppingCartNotFound_ThrowsException() {
        User user = TestUtil.createUser();
        Long cartItemId = 1L;
        UpdateCartItemRequestDto requestDto = TestUtil.createUpdateCartItemRequestDto();

        CartItem cartItem = TestUtil.createCartItem();

        when(cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, user.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.updateBookQuantityInCart(user, cartItemId, requestDto));

        assertEquals("Can't find shopping cart by user id: " + user.getId(),
                ex.getMessage());
        verify(cartItemRepository).findByIdAndShoppingCartUserId(cartItemId, user.getId());
        verify(shoppingCartRepository).findByUser(user);
        verifyNoMoreInteractions(shoppingCartMapper);
    }

    @Test
    @DisplayName("Should throw exception if cart item is not found")
    void deleteBookFromCart_InvalidCartItem_ThrowsException() {
        User user = TestUtil.createUser();
        Long cartItemId = 42L;

        when(cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, user.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.deleteBookFromCart(user, cartItemId));

        assertEquals("Can't find cart item by id: 42", ex.getMessage());
        verify(cartItemRepository).findByIdAndShoppingCartUserId(cartItemId, user.getId());
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("Should delete cart item when it exists")
    void deleteBookFromCart_ValidCartItem_DeletesSuccessfully() {
        User user = TestUtil.createUser();
        Long cartItemId = 1L;

        CartItem cartItem = TestUtil.createCartItem();

        when(cartItemRepository.findByIdAndShoppingCartUserId(cartItemId, user.getId()))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.deleteBookFromCart(user, cartItemId);

        verify(cartItemRepository).findByIdAndShoppingCartUserId(cartItemId, user.getId());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    @DisplayName("Should throw exception when shopping cart is not found")
    void clearShoppingCart_ShoppingCartNotFound_ThrowsException() {
        User user = TestUtil.createUser();

        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex =
                assertThrows(EntityNotFoundException.class,
                        () -> shoppingCartService.clearShoppingCart(user));

        assertEquals("Can't find shopping cart by user id: " + user.getId(),
                ex.getMessage());
        verify(shoppingCartRepository).findByUser(user);
        verifyNoMoreInteractions(cartItemRepository, shoppingCartMapper);
    }

    @Test
    @DisplayName("Should clear cart items when it exists")
    void clearShoppingCart_ValidShoppingCart_EmptiesCart() {
        User user = TestUtil.createUser();
        Set<CartItem> items = new HashSet<>();
        items.add(TestUtil.createCartItem());

        ShoppingCart shoppingCart = TestUtil.createShoppingCart();
        shoppingCart.setCartItems(items);
        when(shoppingCartRepository.findByUser(user))
                .thenReturn(Optional.of(shoppingCart));

        shoppingCartService.clearShoppingCart(user);

        assertTrue(shoppingCart.getCartItems().isEmpty());
        verify(shoppingCartRepository).findByUser(user);
    }
}
