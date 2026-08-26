package com.api.order.service;

import com.api.order.client.CartClient;
import com.api.order.client.ProductClient;
import com.api.order.client.UserClient;
import com.api.order.dto.CartItemResponse;
import com.api.order.dto.OrderItemResponse;
import com.api.order.dto.OrderRequest;
import com.api.order.dto.OrderResponse;
import com.api.order.dto.OrderStatusRequest;
import com.api.order.dto.ProductResponse;
import com.api.order.model.Order;
import com.api.order.model.OrderItem;
import com.api.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final CartClient cartClient;

    public Optional<OrderResponse> createOrder(OrderRequest request) {
        if (request == null || request.getUserId() == null
                || userClient.findById(request.getUserId()) == null) {
            return Optional.empty();
        }

        List<CartItemResponse> cartItems = cartClient.getCartItems(request.getUserId());
        if (cartItems.isEmpty()) {
            return Optional.empty();
        }

        Map<Long, ProductResponse> products = new HashMap<>();
        for (CartItemResponse cartItem : cartItems) {
            ProductResponse product = productClient.findById(cartItem.getProductId());
            if (!isValidCartItem(cartItem, product)) {
                return Optional.empty();
            }
            products.put(cartItem.getProductId(), product);
        }

        for (CartItemResponse cartItem : cartItems) {
            if (!productClient.decreaseStock(cartItem.getProductId(), cartItem.getQuantity())) {
                return Optional.empty();
            }
        }

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(com.api.order.model.OrderStatus.PENDING);
        order.setFinalPrice(BigDecimal.ZERO);

        BigDecimal finalPrice = BigDecimal.ZERO;
        for (CartItemResponse cartItem : cartItems) {
            ProductResponse product = products.get(cartItem.getProductId());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setTotalPrice(itemTotal);
            order.getItems().add(orderItem);
            finalPrice = finalPrice.add(itemTotal);
        }

        order.setFinalPrice(finalPrice);
        Order savedOrder = orderRepository.save(order);
        cartClient.clearCart(request.getUserId());
        return Optional.of(mapToResponse(savedOrder));
    }

    public Optional<OrderResponse> getOrder(Long orderId) {
        return orderRepository.findById(orderId).map(this::mapToResponse);
    }

    public List<OrderResponse> getOrdersForUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<OrderResponse> updateStatus(Long orderId, OrderStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return Optional.empty();
        }
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus(request.getStatus());
            return mapToResponse(orderRepository.save(order));
        });
    }

    private boolean isValidCartItem(CartItemResponse cartItem, ProductResponse product) {
        return cartItem != null
                && cartItem.getProductId() != null
                && cartItem.getQuantity() != null
                && cartItem.getQuantity() > 0
                && product != null
                && Boolean.TRUE.equals(product.getActive())
                && product.getPrice() != null
                && product.getStockQuantity() != null
                && product.getStockQuantity() >= cartItem.getQuantity();
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus());
        response.setFinalPrice(order.getFinalPrice());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setItems(order.getItems().stream().map(this::mapToItemResponse).toList());
        return response;
    }

    private OrderItemResponse mapToItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setTotalPrice(item.getTotalPrice());
        return response;
    }
}
