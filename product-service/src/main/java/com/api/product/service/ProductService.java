package com.api.product.service;

import com.api.product.dto.ProductRequest;
import com.api.product.dto.ProductResponse;
import com.api.product.dto.StockUpdateRequest;
import com.api.product.model.Product;
import com.api.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        updateProductFromRequest(product, request);
        return mapToResponse(productRepository.save(product));
    }

    public Optional<ProductResponse> getProduct(Long id) {
        return productRepository.findById(id).map(this::mapToResponse);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest request) {
        return productRepository.findById(id).map(existing -> {
            updateProductFromRequest(existing, request);
            return mapToResponse(productRepository.save(existing));
        });
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByActiveTrue().stream().map(this::mapToResponse).toList();
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setActive(false);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProductBy(keyword).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public Optional<ProductResponse> decreaseStock(Long id, StockUpdateRequest request) {
        if (request == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            return Optional.empty();
        }

        return productRepository.findById(id)
                .filter(product -> Boolean.TRUE.equals(product.getActive()))
                .filter(product -> product.getStockQuantity() != null
                        && product.getStockQuantity() >= request.getQuantity())
                .map(product -> {
                    product.setStockQuantity(product.getStockQuantity() - request.getQuantity());
                    return mapToResponse(productRepository.save(product));
                });
    }

    private void updateProductFromRequest(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());
        product.setImageUrl(request.getImageUrl());
        if (request.getActive() != null) {
            product.setActive(request.getActive());
        }
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());
        return response;
    }
}
