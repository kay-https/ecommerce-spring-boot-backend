package com.kayllanne.ecommerce.controller;

import com.kayllanne.ecommerce.dto.CartItemRequest;
import com.kayllanne.ecommerce.dto.CartItemResponse;
import com.kayllanne.ecommerce.dto.CartResponse;
import com.kayllanne.ecommerce.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_CLIENT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CartResponse> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cart = cartService.getOrCreateCart(userDetails.getUsername());
        return ResponseEntity.ok(cart);
    }
    @PostMapping("/items")
    @PreAuthorize("hasAuthority('ROLE_CLIENT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CartItemResponse> addOrUpdateItemToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CartItemRequest request) {
        CartItemResponse cartItem = cartService.addProductToCart(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeCartItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        cartService.removeProductFromCart(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/items/{productId}")
    @PreAuthorize("hasAuthority('ROLE_CLIENT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        CartItemResponse cartItem = cartService.updateCartItemQuantity(userDetails.getUsername(), productId, quantity);
        if (cartItem == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cartItem);
    }
}