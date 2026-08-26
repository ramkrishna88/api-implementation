package com.api.order.client;

import com.api.order.dto.ProductResponse;
import com.api.order.dto.StockUpdateRequest;
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

    public boolean decreaseStock(Long productId, int quantity) {
        try {
            restClient.patch()
                    .uri("/api/products/{id}/stock", productId)
                    .body(new StockUpdateRequest(quantity))
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException exception) {
            return false;
        }
    }
}
