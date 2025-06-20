package com.kayllanne.ecommerce.service;

import com.kayllanne.ecommerce.dto.CartItemRequest;
import com.kayllanne.ecommerce.dto.CartItemResponse;
import com.kayllanne.ecommerce.dto.CartResponse;
import com.kayllanne.ecommerce.entity.Cart;
import com.kayllanne.ecommerce.entity.CartItem;
import com.kayllanne.ecommerce.entity.CartItemId;
import com.kayllanne.ecommerce.entity.Product;
import com.kayllanne.ecommerce.entity.User;
import com.kayllanne.ecommerce.exception.ResourceNotFoundException;
import com.kayllanne.ecommerce.repository.CartItemRepository;
import com.kayllanne.ecommerce.repository.CartRepository;
import com.kayllanne.ecommerce.repository.ProductRepository;
import com.kayllanne.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @Transactional
    public CartResponse getOrCreateCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart newCart = Cart.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return cartRepository.save(newCart);
                });
        return mapToCartResponse(cart);
    }

    // Método para adicionar um produto ao carrinho ou atualizar sua quantidade
    @Transactional
    public CartItemResponse addProductToCart(String username, CartItemRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + username));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + request.getProductId()));


        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setPrice(product.getPrice());
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .price(product.getPrice())
                    .addedAt(LocalDateTime.now())
                    .build();
            cart.getCartItems().add(cartItem);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartItemResponse(cartItem);
    }

    // Método para remover um item do carrinho
    @Transactional
    public void removeProductFromCart(String username, Long productId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + username));

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho para o produto ID: " + productId));

        cart.getCartItems().remove(itemToRemove);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    // Método para atualizar a quantidade de um item no carrinho
    @Transactional
    public CartItemResponse updateCartItemQuantity(String username, Long productId, Integer newQuantity) {
        if (newQuantity <= 0) {
            removeProductFromCart(username, productId);
            return null;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + username));

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho para o produto ID: " + productId));

        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(cartItem.getProduct().getPrice());
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
        return mapToCartItemResponse(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado para o usuário: " + username));

        return mapToCartResponse(cart);
    }
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal totalAmount = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .username(cart.getUser().getUsername())
                .items(itemResponses)
                .totalAmount(totalAmount)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }
    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .imageUrl(cartItem.getProduct().getImageUrl())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .subtotal(cartItem.getSubtotal())
                .build();
    }
}