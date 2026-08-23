package com.api_implementation.order.service;

import com.api_implementation.cart.modal.CartItem;
import com.api_implementation.cart.repository.CartItemRepository;
import com.api_implementation.order.dto.OrderItemResponse;
import com.api_implementation.order.dto.OrderRequest;
import com.api_implementation.order.dto.OrderResponse;
import com.api_implementation.order.dto.OrderStatusRequest;
import com.api_implementation.order.modal.Order;
import com.api_implementation.order.modal.OrderItem;
import com.api_implementation.order.repository.OrderRepository;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public Optional<OrderResponse> createOrder(OrderRequest request) {
        if (request == null || request.getUserId() == null) {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.findById(request.getUserId());
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null
                    || !Boolean.TRUE.equals(product.getActive())
                    || product.getPrice() == null
                    || product.getStockQuantity() == null
                    || cartItem.getQuantity() == null
                    || cartItem.getQuantity() <= 0
                    || product.getStockQuantity() < cartItem.getQuantity()) {
                return Optional.empty();
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setFinalPrice(BigDecimal.ZERO);

        BigDecimal finalPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = product.getPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(itemTotal);
            order.getItems().add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            finalPrice = finalPrice.add(itemTotal);
        }

        order.setFinalPrice(finalPrice);
        productRepository.saveAll(cartItems.stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toList()));

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    public Optional<OrderResponse> getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToOrderResponse);
    }

    public Optional<List<OrderResponse>> getOrdersForUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                        .map(this::mapToOrderResponse)
                        .collect(Collectors.toList()));
    }

    public Optional<OrderResponse> updateStatus(Long orderId, OrderStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return Optional.empty();
        }

        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setStatus(request.getStatus());
                    return mapToOrderResponse(orderRepository.save(order));
                });
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus());
        response.setFinalPrice(order.getFinalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setItems(order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList()));
        return response;
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProduct().getName());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        return response;
    }
}
