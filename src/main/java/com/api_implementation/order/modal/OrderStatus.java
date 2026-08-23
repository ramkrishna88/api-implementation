package com.api_implementation.order.modal;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    @JsonCreator
    public static OrderStatus fromValue(String value) {
        if (value == null) {
            return null;
        }

        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
