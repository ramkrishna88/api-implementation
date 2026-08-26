package com.api.order.client;

import com.api.order.dto.CartItemResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class CartClient {

    private final RestClient restClient;

    public CartClient(@Value("${cart.service.url}") String cartServiceUrl) {
        this.restClient = RestClient.create(cartServiceUrl);
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        try {
            List<CartItemResponse> response = restClient.get()
                    .uri("/api/cart")
                    .header("X-User-ID", userId.toString())
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            return response == null ? List.of() : response;
        } catch (RestClientException exception) {
            return List.of();
        }
    }

    public boolean clearCart(Long userId) {
        try {
            restClient.delete()
                    .uri("/api/cart")
                    .header("X-User-ID", userId.toString())
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException exception) {
            return false;
        }
    }
}
