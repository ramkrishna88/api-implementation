package com.api.cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class UserClient {

    private final RestClient restClient;

    public UserClient(@Value("${user.service.url}") String userServiceUrl) {
        this.restClient = RestClient.create(userServiceUrl);
    }

    public boolean exists(Long userId) {
        try {
            restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException exception) {
            return false;
        }
    }
}
