package com.api.cart.controller;

import com.api.cart.dto.CartItemRequest;
import com.api.cart.dto.CartItemResponse;
import com.api.cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") Long userId,
            @RequestBody CartItemRequest request) {
        if (!cartItemService.addToCart(userId, request)) {
            return ResponseEntity.badRequest()
                    .body("Product is unavailable, out of stock, or the request is invalid");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") Long userId,
            @PathVariable Long productId) {
        return cartItemService.deleteItemFromCart(userId, productId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCartItems(
            @RequestHeader("X-User-ID") Long userId) {
        return cartItemService.getCartItems(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-ID") Long userId) {
        cartItemService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
