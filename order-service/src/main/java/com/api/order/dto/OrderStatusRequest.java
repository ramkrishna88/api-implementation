package com.api.order.dto;

import com.api.order.model.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusRequest {
    private OrderStatus status;
}
