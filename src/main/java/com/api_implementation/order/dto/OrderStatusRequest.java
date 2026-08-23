package com.api_implementation.order.dto;

import com.api_implementation.order.modal.OrderStatus;
import lombok.Data;

@Data
public class OrderStatusRequest {
    private OrderStatus status;
}
