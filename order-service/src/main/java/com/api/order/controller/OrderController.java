package com.api.order.controller;

import com.api.order.dto.OrderRequest;
import com.api.order.dto.OrderResponse;
import com.api.order.dto.OrderStatusRequest;
import com.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(order))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long orderId,
            @RequestBody(required = false) OrderStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest()
                    .body("Request body must contain a status: PENDING, CONFIRMED, SHIPPED, DELIVERED, or CANCELLED");
        }
        return orderService.updateStatus(orderId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
