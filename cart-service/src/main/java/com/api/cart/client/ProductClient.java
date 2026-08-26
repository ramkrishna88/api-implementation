package com.api.cart.client;

import com.api.cart.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(@Value("${product.service.url}") String productServiceUrl) {
        this.restClient = RestClient.create(productServiceUrl);
    }

    public ProductResponse findById(Long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponse.class);
        } catch (RestClientException exception) {
            return null;
        }
    }
}
