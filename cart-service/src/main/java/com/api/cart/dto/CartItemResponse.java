package com.api.cart.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
