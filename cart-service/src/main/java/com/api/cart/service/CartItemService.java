package com.api.cart.service;

import com.api.cart.client.ProductClient;
import com.api.cart.client.UserClient;
import com.api.cart.dto.CartItemRequest;
import com.api.cart.dto.CartItemResponse;
import com.api.cart.dto.ProductResponse;
import com.api.cart.model.CartItem;
import com.api.cart.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService {

    private final ProductClient productClient;
    private final UserClient userClient;
    private final CartItemRepository cartItemRepository;

    public boolean addToCart(Long userId, CartItemRequest request) {
        if (userId == null || request == null || request.getProductId() == null
                || request.getQuantity() == null || request.getQuantity() <= 0
                || !userClient.exists(userId)) {
            return false;
        }

        ProductResponse product = productClient.findById(request.getProductId());
        if (!isAvailable(product)) {
            return false;
        }

        CartItem existing = cartItemRepository.findByUserIdAndProductId(userId, product.getId());
        int newQuantity = request.getQuantity() + (existing == null ? 0 : existing.getQuantity());
        if (product.getStockQuantity() < newQuantity) {
            return false;
        }

        CartItem cartItem = existing == null ? new CartItem() : existing;
        cartItem.setUserId(userId);
        cartItem.setProductId(product.getId());
        cartItem.setProductName(product.getName());
        cartItem.setQuantity(newQuantity);
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
        cartItemRepository.save(cartItem);
        return true;
    }

    public Optional<List<CartItemResponse>> getCartItems(Long userId) {
        if (userId == null || !userClient.exists(userId)) {
            return Optional.empty();
        }
        return Optional.of(cartItemRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList());
    }

    public boolean deleteItemFromCart(Long userId, Long productId) {
        if (userId == null || productId == null || !userClient.exists(userId)) {
            return false;
        }
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            return false;
        }
        cartItemRepository.delete(cartItem);
        return true;
    }

    public void clearCart(Long userId) {
        if (userId != null) {
            cartItemRepository.deleteByUserId(userId);
        }
    }

    private boolean isAvailable(ProductResponse product) {
        return product != null
                && Boolean.TRUE.equals(product.getActive())
                && product.getPrice() != null
                && product.getStockQuantity() != null
                && product.getStockQuantity() > 0;
    }

    private CartItemResponse mapToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(cartItem.getId());
        response.setProductId(cartItem.getProductId());
        response.setProductName(cartItem.getProductName());
        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());
        return response;
    }
}
