package com.api.order.client;

import com.api.order.dto.UserResponse;
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

    public UserResponse findById(Long userId) {
        try {
            return restClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .body(UserResponse.class);
        } catch (RestClientException exception) {
            return null;
        }
    }
}
