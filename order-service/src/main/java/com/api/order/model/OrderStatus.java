package com.api.order.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED;

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        return value == null ? null : valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
