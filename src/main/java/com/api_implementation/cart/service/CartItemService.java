package com.api_implementation.cart.service;

import com.api_implementation.cart.dto.CartItemRequest;
import com.api_implementation.cart.dto.CartItemResponse;
import com.api_implementation.cart.modal.CartItem;
import com.api_implementation.cart.repository.CartItemRepository;
import com.api_implementation.product.modal.Product;
import com.api_implementation.product.repository.ProductRepository;
import com.api_implementation.user.model.User;
import com.api_implementation.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CartItemService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public boolean addToCart(Long userId, CartItemRequest cartItemRequest) {
        if (cartItemRequest == null
                || cartItemRequest.getProductId() == null
                || cartItemRequest.getQuantity() == null
                || cartItemRequest.getQuantity() <= 0) {
            return false;
        }

        Optional<Product> productOpt = productRepository.findById(cartItemRequest.getProductId());
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();
        if (!Boolean.TRUE.equals(product.getActive())
                || product.getStockQuantity() == null
                || product.getStockQuantity() < cartItemRequest.getQuantity()) {
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);

        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + cartItemRequest.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
            cartItemRepository.save(cartItem);
        }
        return true;
    }

    public Optional<List<CartItemResponse>> getCartItems(Long userId) {
        return userRepository.findById(userId)
                .map(user -> cartItemRepository.findByUser(user).stream()
                        .map(this::mapToCartItemResponse)
                        .collect(Collectors.toList()));
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setCartItemId(cartItem.getId());
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setQuantity(cartItem.getQuantity());
        response.setPrice(cartItem.getPrice());
        return response;
    }

    public boolean deleteItemFromCart(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt = userRepository.findById(userId);

        if (productOpt.isPresent() && userOpt.isPresent()) {
            CartItem cartItem = cartItemRepository.findByUserAndProduct(userOpt.get(), productOpt.get());
            if (cartItem == null) {
                return false;
            }
            cartItemRepository.delete(cartItem);
            return true;
        }

        return false;

    }
}
